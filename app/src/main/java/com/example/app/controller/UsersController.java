package com.example.app.controller;

import android.view.View;

import com.example.app.SortType;
import com.example.app.User;
import com.example.app.UserAdapter;
import com.example.app.UsersActivity;
import com.example.app.model.UsersModel;

import java.util.ArrayList;

public class UsersController {

    private UsersActivity usersActivity;
    private UsersModel usersModel;
    private UserAdapter adapter;

    public UsersController(UsersActivity usersActivity, UserAdapter adapter){
        this.adapter = adapter;
        this.usersActivity = usersActivity;
        usersModel = new UsersModel(this);
    }

    public void bMessage(View view, UserAdapter adapter, ArrayList<User> usersList) {
        usersModel.bMessage(view, adapter,usersList);
    }

    public void bNameFilter(UserAdapter adapter, ArrayList<User> usersList, SortType type) {
        usersModel.bNameFilter(adapter, usersList,type);
    }

    public void bSearch(UserAdapter adapter, ArrayList<User> usersList, String str) {
        usersModel.bSearch(adapter,usersList, str);
    }

    public void bAllFilter(UserAdapter adapter, ArrayList<User> usersList) {
        usersModel.bAllFilter(adapter,usersList);
    }
}
