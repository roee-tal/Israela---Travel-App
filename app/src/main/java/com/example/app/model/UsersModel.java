package com.example.app.model;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.app.SortType;
import com.example.app.User;
import com.example.app.UserAdapter;
import com.example.app.controller.UsersController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class UsersModel {

    UsersController usersController;

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    public UsersModel(UsersController usersController){
        this.usersController = usersController;

    }

    public void bMessage(View view, UserAdapter adapter, ArrayList<User> usersList) {
        usersList.clear();
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            usersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                int num = Integer.parseInt(mes);
                                if ((num>0)) {
                                    usersList.add(u);
                                }
                            }
                        }
                        Log.d("initSearchWidgets", "UsersList.size="+usersList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "UsersList.size="+usersList.size());
                    }
                });
        Collections.sort(usersList, User.mesAscending);
    }

    public void bNameFilter(UserAdapter adapter, ArrayList<User> UsersList, SortType type) {

        UsersList.clear();
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                UsersList.add(u);
                                Collections.sort(UsersList, User.nameAscending);

//                                if (type != null)
//                                    sortList(type);
                                Log.d("allFilterTapped", "UsersList.size=" + UsersList.size());
                                adapter.notifyDataSetChanged();
                                Log.d("allFilterTapped", "UsersList.size=" + UsersList.size());
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
//                    private void sortList(SortType type) {
//                        switch (type) {
//                            case UserNAmeDown2Up:
//                                Collections.sort(UsersList, User.nameAscending);
//
//                                break;
//                            case MessageUp2Down:
//                                Collections.sort(UsersList, User.mesAscending);
//                                break;
//                            default:
//                                Log.d("sortList", "default !" + type);
//                                break;
//                        }
//                    }
                });
//        Collections.sort(UsersList, User.nameAscending);
    }

    public void bSearch(UserAdapter adapter, ArrayList<User> UsersList, String str) {
        UsersList.clear();
        String currentSearchText = str;
        Log.d("initSearchWidgets", "str="+str);
        fstore = FirebaseFirestore.getInstance();

        fstore.collection("Users")
                .orderBy("Email")
                .startAt(str)
                .endAt(str + "\uf8ff")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            UsersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                UsersList.add(u);
                            }
                        }
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                    }
                });
    }

    public void bAllFilter(UserAdapter adapter, ArrayList<User> UsersList) {
        UsersList.clear();
        fstore = FirebaseFirestore.getInstance();

        fstore.collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            UsersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                UsersList.add(u);
                            }
                        }
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                    }
                });
    }

//    private void setAdapter(ArrayList<User> UsersList)
//    {
//        adapter = new UserAdapter(getApplicationContext(), 0, UsersList);
//        listView.setAdapter(adapter);
//    }
}
