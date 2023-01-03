package com.example.app.model;

import com.example.app.controller.UsersController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersModel {

    UsersController usersController;

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    public UsersModel(UsersController usersController){
        this.usersController = usersController;
    }
}
