package edu.byu.cs.tweeter.server.lambda.feedProcessor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;

public class FeedProcessor implements RequestHandler<SQSEvent, Void> {


    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());
            List<FeedBean> feeds = new ArrayList<FeedBean>();
            feeds = JsonSerializer.deserializeList(msg.getBody(), FeedBean[].class);

            for(FeedBean feed : feeds){
                System.out.println(feed);
            }

//            // TODO:
//            System.out.println(msg.getBody());
            // Add code to print message body to the log
        }
        return null;
    }
}
