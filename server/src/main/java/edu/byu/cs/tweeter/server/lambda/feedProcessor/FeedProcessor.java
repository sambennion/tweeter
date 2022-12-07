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

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.util.Pair;

public class FeedProcessor implements RequestHandler<SQSEvent, Void> {


    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());

            Status status = JsonSerializer.deserialize(msg.getBody(), Status.class);

            FollowDAO followDAO = new FollowDAO();

//            List<String> followersAlias = new ArrayList<>();
            List<FeedBean> feeds = new ArrayList<>();
            boolean hasMoreFollowers = true;
            String lastFollower = null;
            while(hasMoreFollowers){
                Pair<List<User>, Boolean> followerBatch = followDAO.getFollowers(status.getUser().getAlias(), 25, lastFollower);
                for(User follower : followerBatch.getFirst()){
//                    followersAlias.add(follower.getAlias());
                    FeedBean feedBean = new FeedBean();
                    feedBean.setStatus(status.getPost());
                    feedBean.setAlias(follower.getAlias());
                    feedBean.setStatusOwner(status.getUser().getAlias());
                    feedBean.setMentions(status.getMentions());
                    feedBean.setTimestamp(status.getDate());
                    feedBean.setUrls(status.getUrls());
//                    System.out.println("Adding item to feed table");
                    feeds.add(feedBean);
                }

                hasMoreFollowers = followerBatch.getSecond();

                lastFollower = feeds.get(feeds.size()-1).getAlias();

                if(feeds.size() > 0){
                    String message = JsonSerializer.serialize(feeds);

                    String queueUrl = "https://sqs.us-west-2.amazonaws.com/321965405476/FeedBatchQueue";
                    System.out.println("Sending message to " + queueUrl);
                    SendMessageRequest send_msg_request = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(message);

                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                    SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

                    String msgId = send_msg_result.getMessageId();
                    System.out.println("Message ID: " + msgId);

                    feeds = new ArrayList<>();
                }
                else{
                    System.out.println("No more in batch");
                }


//                hasMoreFollowers = followerBatch.getSecond();
//                lastFollower = followerBatch.getFirst().get(followerBatch.getFirst().size()-1).getAlias();
//
//                FeedBean feedBean = new FeedBean();
//                feedBean.setStatus(status.getPost());
//                feedBean.setAlias();
//                feedBean.setStatusOwner(status.getUser().getAlias());
//                feedBean.setMentions(status.getMentions());
//                feedBean.setTimestamp(status.getDate());
//                feedBean.setUrls(status.getUrls());
//                System.out.println("Adding item to feed table");

//                if(f.size() > 0){
//                    String message = JsonSerializer.serialize(batch);
//
//                    String queueUrl = "https://sqs.us-west-2.amazonaws.com/321965405476/FeedBatchQueue";
//                    System.out.println("Sending message to " + queueUrl);
//                    SendMessageRequest send_msg_request = new SendMessageRequest()
//                            .withQueueUrl(queueUrl)
//                            .withMessageBody(message);
//
//                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
//                    SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
//
//                    String msgId = send_msg_result.getMessageId();
//                    System.out.println("Message ID: " + msgId);
//
//                    batch = new ArrayList<>();
//                }
//                else{
//                    System.out.println("No more in batch");
//                }

            }
        }









        //for processing feed posts
//        for (SQSEvent.SQSMessage msg : event.getRecords()) {
//            System.out.println(msg.getBody());
////            List<FeedBean> feeds = new ArrayList<FeedBean>();
//            List<FeedBean> feeds = JsonSerializer.deserializeList(msg.getBody(), FeedBean[].class);
//            List<FeedBean> batch = new ArrayList<>();
//            for(FeedBean feed : feeds){
//                System.out.println("Adding feed to batch " + feed);
//                batch.add(feed);
//
//                if(batch.size() > 0){
//                    String message = JsonSerializer.serialize(batch);
//
//                    String queueUrl = "https://sqs.us-west-2.amazonaws.com/321965405476/FeedBatchQueue";
//                    System.out.println("Sending message to " + queueUrl);
//                    SendMessageRequest send_msg_request = new SendMessageRequest()
//                            .withQueueUrl(queueUrl)
//                            .withMessageBody(message);
//
//                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
//                    SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
//
//                    String msgId = send_msg_result.getMessageId();
//                    System.out.println("Message ID: " + msgId);
//
//                    batch = new ArrayList<>();
//                }
//                else{
//                    System.out.println("No more in batch");
//                }
//            }
//        }
        return null;
    }
}
