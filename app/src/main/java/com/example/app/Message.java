package com.example.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

public class Message {

    private String text, id;

    public Message(String text, String id){
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public String getid() {
        return this.id;
    }

    public void setid(String id){
        this.id = id;
    }

    public static Comparator<Message> nameAscending = new Comparator<Message>()
    {
        @Override
        public int compare(Message shape1, Message shape2)
        {
            String name1 = shape1.getText();
            String name2 = shape2.getText();
            name1 = name1.toLowerCase();
            name2 = name2.toLowerCase();

            return name1.compareTo(name2);
        }
    };
}