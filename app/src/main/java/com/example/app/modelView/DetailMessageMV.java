package com.example.app.modelView;

import android.app.Activity;
import android.content.Intent;

import com.example.app.View.DetailMessageActivity;
import com.example.app.View.UsersActivity;
import com.example.app.model.DetailMessageModel;

public class DetailMessageMV {

    DetailMessageActivity detailMessageActivity;
    DetailMessageModel detailMessageModel;

    public DetailMessageMV(DetailMessageActivity detailMessageActivity){
        this.detailMessageActivity = detailMessageActivity;
        detailMessageModel = new DetailMessageModel(this);

    }


    public void bDelete(String parsedStringID) {
        detailMessageModel.bDelete(parsedStringID);
    }

    public void areYouSureMessage(String parsedMail, String uId) {
        detailMessageActivity.areYouSureMessage(parsedMail,uId);
    }

    public void bAreYouSureLogic(Activity activity, String parsedMail, String id) {
        detailMessageModel.bAreYouSureLogic(activity,parsedMail,id);
    }

    public void ShowUsers() {
        detailMessageActivity.startActivity(new Intent(detailMessageActivity, UsersActivity.class));
    }
}
