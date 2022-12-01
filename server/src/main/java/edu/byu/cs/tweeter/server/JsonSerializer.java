package edu.byu.cs.tweeter.server;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class JsonSerializer {

    public static String serialize(Object requestInfo){
        return new Gson().toJson(requestInfo);
    }

    public static <T> List<T> deserializeList(String value, Class<T[]> returnType){
        T[] arr = new Gson().fromJson(value, returnType);
        return Arrays.asList(arr);
    }

    public static <T> T deserialize(String value, Class<T> returnType){
        return new Gson().fromJson(value, returnType);
    }
}
