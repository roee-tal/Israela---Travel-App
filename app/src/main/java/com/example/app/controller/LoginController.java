package com.example.app.controller;

import android.content.Intent;
import android.widget.Toast;

import com.example.app.AdminActivity;
import com.example.app.LoginActivity;
import com.example.app.model.LoginModel;

public class LoginController {

    private LoginActivity loginActivity;
    private LoginModel loginModel;

    public LoginController(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        loginModel = new LoginModel(this);
    }

    public void bCreate(String mail, String password) {
        loginModel.bCreate(mail,password);
    }

    public void showToast(String message) {
        Toast.makeText(loginActivity, message, Toast.LENGTH_LONG).show();
    }

    public void showAdminActivity() {
        loginActivity.startActivity(new Intent(loginActivity, AdminActivity.class));
        loginActivity.finish();
    }
}
