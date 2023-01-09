package com.example.app.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.DetailUserActivity;
import com.example.app.User;
import com.example.app.UsersActivity;
import com.example.app.controller.DetailUserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailUserModel {

    private FirebaseFirestore fstore;
    DetailUserController detailUserController;

    public DetailUserModel(DetailUserController detailUserController){
        this.detailUserController = detailUserController;
    }

    public void bDelete(String parsedID) {
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Users").document(parsedID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    String email = doc.getString("Email");
                    String id = doc.getString("ID");
                    String user = doc.getString("isUser");
                    User blockUser = new User(email,id,user);
                    Log.d("TAG", blockUser.getId());
                    fstore.collection("BlockedUsers").document(parsedID).set(blockUser);
                    fstore.collection("Users").document(parsedID).delete();
                    detailUserController.showToast("removed");
//                    Toast.makeText(DetailUserActivity.this, "removed", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(DetailUserActivity.this, UsersActivity.class));
                    detailUserController.showUserActivity();
                }
                else{
                    detailUserController.showToast("dont exist");
//                    Toast.makeText(DetailUserActivity.this, "dont exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
