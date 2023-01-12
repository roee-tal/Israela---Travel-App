package com.example.app.modelView;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.app.activities.LoginActivity;
import com.example.app.activities.StartActivity;
import com.example.app.modelView.helpClasses.ShowToastAndSignOut;
import com.example.app.model.LoginModel;

public class LoginMV {

    private LoginActivity loginActivity;
    private LoginModel loginModel;

    public LoginMV(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        loginModel = new LoginModel(this);
        ShowToastAndSignOut showToastAndSignOut = new ShowToastAndSignOut();

    }

    public void bCreate(String mail, String password) {
        if(TextUtils.isEmpty(mail)|| TextUtils.isEmpty(password)){
            showToast("Empty");
        }
        // PAssword has to be at least 9 chars
        else if(password.length()<9){
            showToast("you need more than 9 chars");
        }
        else{
            loginModel.notBlocked(mail,password);
        }
    }

    public void showToast(String message) {
        Toast.makeText(loginActivity, message, Toast.LENGTH_LONG).show();
    }

    public void showStartActivity() {
        loginActivity.startActivity(new Intent(loginActivity, StartActivity.class));
        loginActivity.finish();
    }
}
