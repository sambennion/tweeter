package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    /**
     * Value of the auth token.
     */
    public String token;
    /**
     * String representation of date/time at which the auth token was created.
     */
    public String datetime;

    public AuthToken() {
    }

    public AuthToken(String token) {
        this.token = token;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
//        System.out.println(dateFormat.format(date));
        datetime = dateFormat.format(date);
    }

    public AuthToken(String token, String datetime) {
        this.token = token;
        this.datetime = datetime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }
}