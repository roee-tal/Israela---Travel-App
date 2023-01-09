package com.example.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.app.DetailUserActivity;
import com.example.app.StartActivity;
import com.example.app.User;
import com.example.app.UsersActivity;
import com.example.app.model.DetailUserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.ExecutionException;

public class DetailUserController {

    DetailUserActivity detailUserActivity;
    DetailUserModel detailUserModel;
    ShowToastAndSignOut showToastAndSignOut;


    public DetailUserController(DetailUserActivity detailUserActivity){
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

    public void bDelete(String parsedStringID) {
        detailUserModel.bDelete(parsedStringID);
    }
}
