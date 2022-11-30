package edu.byu.cs.tweeter.server.lambda;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.server.service.injectorModule.ServiceModule;

public class Handler {
    protected Injector injector;
    public Handler(){
        this.injector = Guice.createInjector(new ServiceModule());
    }
}
