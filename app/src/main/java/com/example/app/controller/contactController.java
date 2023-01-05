package com.example.app.controller;

import android.content.Intent;
import android.widget.Toast;

import com.example.app.AdminActivity;
import com.example.app.ContactActivity;
import com.example.app.MainActivity;
import com.example.app.model.contactModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class contactController {
    private contactModel contactmodel;
    private ContactActivity contactActivity;

    public contactController(ContactActivity contactActivity){
        this.contactActivity = contactActivity;
        contactmodel = new contactModel(this);
    }


    public void bSend(String s) {
        contactmodel.bSend(s);
    }

    public void showMainActivity() {
        contactActivity.startActivity(new Intent(contactActivity, MainActivity.class));
        contactActivity.finish();
    }

    public void showToast(String message) {
        Toast.makeText(contactActivity, message, Toast.LENGTH_LONG).show();
    }
}
