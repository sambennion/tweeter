package edu.byu.cs.tweeter.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;

public abstract class Service {

    protected void runTask(BackgroundTask task){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(task);
    }
    protected void runTwoTasks(BackgroundTask task1, BackgroundTask task2){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(task1);
        executor.execute(task2);
    }

}
