package com.example.app.controller;

import com.example.app.ContactAdminActivity;
import com.example.app.Message;
import com.example.app.MessageAdapter;
import com.example.app.model.ContactAdminModel;

import java.util.ArrayList;

public class ContactAdminController {

    ContactAdminActivity contactAdminActivity;
    ContactAdminModel contactAdminModel;
    MessageAdapter adapter;

    public ContactAdminController(ContactAdminActivity contactAdminActivity, MessageAdapter adapter){
        this.contactAdminActivity = contactAdminActivity;
        this.adapter = adapter;
        contactAdminModel = new ContactAdminModel(this);
    }

    public void showList(MessageAdapter adapter, String text, ArrayList<Message> messList) {
        contactAdminModel.showList(adapter, text, messList);
    }
}
