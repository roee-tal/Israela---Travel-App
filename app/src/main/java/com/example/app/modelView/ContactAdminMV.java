package com.example.app.modelView;

import com.example.app.activities.ContactAdminActivity;
import com.example.app.model.objects.Message;
import com.example.app.modelView.adapters.MessageAdapter;
import com.example.app.model.ContactAdminModel;

import java.util.ArrayList;

public class ContactAdminMV {

    ContactAdminActivity contactAdminActivity;
    ContactAdminModel contactAdminModel;
    MessageAdapter adapter;

    public ContactAdminMV(ContactAdminActivity contactAdminActivity, MessageAdapter adapter){
        this.contactAdminActivity = contactAdminActivity;
        this.adapter = adapter;
        contactAdminModel = new ContactAdminModel(this);
    }

    public void showList(MessageAdapter adapter, String text, ArrayList<Message> messList) {
        contactAdminModel.showList(adapter, text, messList);
    }

    public void notifyAdapter(MessageAdapter adapter) {
        contactAdminActivity.notifyAdapter(adapter);
    }
}
