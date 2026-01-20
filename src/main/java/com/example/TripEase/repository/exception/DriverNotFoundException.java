package com.example.TripEase.exception;

public class DriverNotFoundException extends  RuntimeException{

    public  DriverNotFoundException(String message){
        super(message);
    }
}
