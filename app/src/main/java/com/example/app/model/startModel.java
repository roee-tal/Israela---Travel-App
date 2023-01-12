package com.example.app.model;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import com.example.app.modelView.startMV;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class startModel extends AppCompatActivity {

        private startMV startMv;
        private FirebaseAuth auth;
        private FirebaseFirestore fstore;


        public startModel(startMV controller){
            this.startMv = controller;
            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
        }

        //Check if the user is not blocked
        public void notBlocked(String e_mail, String pass) {
            fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
                  .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                              QuerySnapshot querySnapshot = task.getResult();
                                  int size = querySnapshot.size();
                                  //If the query found the user in the document do not enter the app
                                  if(size != 0) {
                                      auth.signOut();
                                      startMv.showToast("You are blocked");
                                  }
                                  else{
                                      startMv.loginU(e_mail, pass);
                                  }
                        }
                                  else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                        }
                                }
                        });
        }

    public void activityRes(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String mail = account.getEmail();
                notBlockedG(mail, account);

            } catch (ApiException e) {
                startMv.showToast("Something went wrong");
            }
        }
    }

    // Check if the user is not blocked(via google). separate the functions because 'account' parameter
    // which I need next
    private void notBlockedG(String e_mail, GoogleSignInAccount account) {

        fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int size = querySnapshot.size();
                            //If the query found the user in the document do not enter the app
                            if(size != 0) {
                                auth.signOut();
                                startMv.showToast( "You are blocked");
                            }
                                else {
                                startMv.firebaseauthwithgoogle(account);
                                }
                            }

                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // If the account is new - we call this func to insert the details
    public void firebaseauthwithgoogle(String uid, String e_mail) {
        DocumentReference df = fstore.collection("Users").document(uid);
        Map<String, Object> us_info = new HashMap<>();
        us_info.put("ID", uid);
        us_info.put("Email", e_mail);
        us_info.put("isUser", "1");
        us_info.put("LettersNum", "0");
        us_info.put("isBlocked", false);
        df.set(us_info);
        FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(us_info);
        startMv.showToast("account created");
    }
}
