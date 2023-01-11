package com.example.app.model;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.app.model.objects.SortType;
import com.example.app.model.objects.User;
import com.example.app.modelView.adapters.UserAdapter;
import com.example.app.modelView.UsersMV;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class UsersModel {

    UsersMV usersMV;

    private FirebaseFirestore fstore;

    public UsersModel(UsersMV usersController){
        this.usersMV = usersController;
        fstore = FirebaseFirestore.getInstance();


    }

    public void bMessage(View view, UserAdapter adapter, ArrayList<User> usersList) {
        usersList.clear();
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
                        usersMV.notifyAdapter(adapter);
                    }
                });
        Collections.sort(usersList, User.mesAscending);
    }

    public void bNameFilter(UserAdapter adapter, ArrayList<User> UsersList, SortType type) {

        UsersList.clear();
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

                                usersMV.notifyAdapter(adapter);
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void bSearch(UserAdapter adapter, ArrayList<User> UsersList, String str) {
        UsersList.clear();
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
                        usersMV.notifyAdapter(adapter);
                    }
                });
    }

    public void bAllFilter(UserAdapter adapter, ArrayList<User> UsersList) {
        UsersList.clear();
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
                        usersMV.notifyAdapter(adapter);
                    }
                });
    }
}
