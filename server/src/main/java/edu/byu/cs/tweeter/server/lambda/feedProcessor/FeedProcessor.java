package edu.byu.cs.tweeter.server.lambda.feedProcessor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;

public class FeedProcessor implements RequestHandler<SQSEvent, Void> {


    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());
//            List<FeedBean> feeds = new ArrayList<FeedBean>();
            List<FeedBean> feeds = JsonSerializer.deserializeList(msg.getBody(), FeedBean[].class);
            List<FeedBean> batch = new ArrayList<>();
            for(FeedBean feed : feeds){
                System.out.println("Adding feed to batch " + feed);
                batch.add(feed);

                if(batch.size() > 0){
                    String message = JsonSerializer.serialize(batch);

                    String queueUrl = "https://sqs.us-west-2.amazonaws.com/321965405476/FeedBatchQueue";
                    System.out.println("Sending message to " + queueUrl);
                    SendMessageRequest send_msg_request = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(message);

                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                    SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

                    String msgId = send_msg_result.getMessageId();
                    System.out.println("Message ID: " + msgId);

                    batch = new ArrayList<>();
                }
                else{
                    System.out.println("No more in batch");
                }
            }
        }
        return null;
    }
}
