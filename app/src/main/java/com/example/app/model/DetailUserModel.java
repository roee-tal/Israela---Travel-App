package com.example.app.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.model.objects.User;
import com.example.app.modelView.DetailUserMV;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailUserModel {

    private FirebaseFirestore fstore;
    DetailUserMV detailUserMV;
    FirebaseAuth auth;

    public DetailUserModel(DetailUserMV detailUserController){
        this.detailUserMV = detailUserController;
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }

    /**
     * Delete the user from the 'users' collection
     * Transfer him to 'blocked users' collection
     * @param parsedID
     */
    public void bDelete(String parsedID) {
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
                    detailUserMV.showToast("removed");
                    detailUserMV.showUserActivity();
                }
                else{
                    detailUserMV.showToast("dont exist");
                }
            }
        });
    }
}
