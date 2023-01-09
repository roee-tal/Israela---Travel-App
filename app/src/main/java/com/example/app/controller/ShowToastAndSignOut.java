package com.example.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.app.AdminActivity;
import com.example.app.StartActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ShowToastAndSignOut {


    public void showToast(Activity activity,String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    public void signOut(GoogleSignInClient gsc, Activity activity) {
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        activity.finish();
                        activity.startActivity(new Intent(activity, StartActivity.class));
                    }
                    else {
                        activity.startActivity(new Intent(activity, StartActivity.class));
                    }
                }


            });



    }
}
