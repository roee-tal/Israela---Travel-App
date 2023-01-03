package com.example.app.controller;

import com.example.app.UsersActivity;
import com.example.app.model.UsersModel;

public class UsersController {

    private UsersActivity usersActivity;
    private UsersModel usersModel;

    public UsersController(UsersActivity usersActivity){
        this.usersActivity = usersActivity;
        usersModel = new UsersModel(this);
    }
}
