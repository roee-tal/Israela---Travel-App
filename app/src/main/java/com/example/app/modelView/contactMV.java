package com.example.app.modelView;

import android.content.Intent;

import com.example.app.activities.ContactActivity;
import com.example.app.activities.MainActivity;
import com.example.app.helpClasses.ShowToastAndSignOut;
import com.example.app.model.contactModel;

public class contactMV {

    private contactModel contactmodel;
    private ContactActivity contactActivity;
    ShowToastAndSignOut showToastAndSignOut;

    public contactMV(ContactActivity contactActivity){
        this.contactActivity = contactActivity;
        contactmodel = new contactModel(this);
        showToastAndSignOut = new ShowToastAndSignOut();
    }

    public void bSend(String s) {
        contactmodel.bSend(s);
    }

    public void showMainActivity() {
        contactActivity.startActivity(new Intent(contactActivity, MainActivity.class));
        contactActivity.finish();
    }

    public void showToast(String message) {
        showToastAndSignOut.showToast(contactActivity,message);
    }
}
