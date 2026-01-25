package com.example.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

public class UserCreatedListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("Received JMS message: " + message);
    }
}