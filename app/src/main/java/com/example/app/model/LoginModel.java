package com.example.app.model;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.LoginActivity;
import com.example.app.StartActivity;
import com.example.app.controller.LoginController;
//import com.example.app.controller.ShowToastAndSignOut;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginModel {

    LoginController loginController;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    public LoginModel(LoginController loginController){
        this.loginController = loginController;
    }

    public void bCreate(String mail, String password) {
        Log.d("dasads",mail);
        if(TextUtils.isEmpty(mail)|| TextUtils.isEmpty(password)){
            loginController.showToast("Empty");
            }
        else if(password.length()<6){
            loginController.showToast("you need more than 6 chars");

        }
        else{
            notBlocked(mail,password);
        }
    }

    private void notBlocked(String e_mail, String pass) {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("BlockedUsers").whereEqualTo("Email",e_mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int size = querySnapshot.size();
                            if (size != 0) {
                                auth.signOut();
                                loginController.showToast("You are blocked");
                            } else {
                                regisuser(e_mail, pass);
                            }
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void regisuser(String txt_email, String pas_email) {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        auth.createUserWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser fu = auth.getCurrentUser();
                    fu.sendEmailVerification();
                    DocumentReference df = fstore.collection("Users").document(fu.getUid());
                    Map<String, Object> us_info = new HashMap<>();
                    us_info.put("ID", fu.getUid());
                    us_info.put("Email", txt_email);
                    us_info.put("isUser", "1");
                    us_info.put("LettersNum", "0");
                    FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(us_info);
                    df.set(us_info);
                    loginController.showToast("success, verify email");
//                    startActivity(new Intent(LoginActivity.this, StartActivity.class));
//                    finish();

                }
                else{
                    loginController.showToast("failed");
//                    Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
