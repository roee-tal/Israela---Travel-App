package com.example.app.modelView;

import android.view.View;

import com.example.app.modelView.helpClassesMV.ShowToastAndSignOut;
import com.example.app.model.objects.SortType;
import com.example.app.model.objects.User;
import com.example.app.modelView.adapters.UserAdapter;
import com.example.app.View.UsersActivity;
import com.example.app.model.UsersModel;

import java.util.ArrayList;

public class UsersMV {

    private UsersActivity usersActivity;
    private UsersModel usersModel;
    private UserAdapter adapter;

    public UsersMV(UsersActivity usersActivity, UserAdapter adapter){
        this.adapter = adapter;
        this.usersActivity = usersActivity;
        usersModel = new UsersModel(this);
        ShowToastAndSignOut showToastAndSignOut = new ShowToastAndSignOut();

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

    public void notifyAdapter(UserAdapter adapter) {
        usersActivity.notifyAdapter(adapter);
    }
}

