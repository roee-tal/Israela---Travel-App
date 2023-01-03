package com.example.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.app.AdminActivity;
import com.example.app.MainActivity;
import com.example.app.StartActivity;
import com.example.app.model.startModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class startController {

    private startModel startmodel;
    private StartActivity view;


    public startController(StartActivity view){
        this.view = view;
        startmodel = new startModel(this);
    }

    public void bLoginMail(String mail, String password) {
        startmodel.bLoginMail(mail, password);
    }

    public void bLoginG(Activity activity) {
        startmodel.bLoginG(activity);
    }



    public void showUserActivity() {
        view.startActivity(new Intent(view, MainActivity.class));
        view.finish();
    }

    public void showAdminActivity() {
        view.startActivity(new Intent(view, AdminActivity.class));
        view.finish();
    }
    public void showToast(String message) {
        Toast.makeText(view, message, Toast.LENGTH_LONG).show();
    }


    public void activityRes(int requestCode, int resultCode, Intent data) {
        startmodel.activityRes(requestCode, resultCode, data);
    }
}
