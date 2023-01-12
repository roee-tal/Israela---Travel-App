package com.example.app.modelView;

import android.content.Intent;

import com.example.app.activities.ContactAdminActivity;
import com.example.app.activities.DetailUserActivity;
import com.example.app.activities.UsersActivity;
import com.example.app.helpClasses.ShowToastAndSignOut;
import com.example.app.model.DetailUserModel;

public class DetailUserMV {

    DetailUserActivity detailUserActivity;
    DetailUserModel detailUserModel;
    ShowToastAndSignOut showToastAndSignOut;


    public DetailUserMV(DetailUserActivity detailUserActivity){
        this.detailUserActivity = detailUserActivity;
        detailUserModel = new DetailUserModel(this);
        showToastAndSignOut = new ShowToastAndSignOut();
    }

    public void showToast(String message){
        showToastAndSignOut.showToast(detailUserActivity,message);
    }

    public void showUserActivity() {
        detailUserActivity.startActivity(new Intent(detailUserActivity, UsersActivity.class));
        detailUserActivity.finish();
    }
    public void showContactAdminActivity(Intent intent) {
        detailUserActivity.startActivity(intent);
        detailUserActivity.finish();
    }

    public void bDelete(String parsedStringID) {
        detailUserModel.bDelete(parsedStringID);
    }
}
