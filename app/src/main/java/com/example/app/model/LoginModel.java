package com.example.app.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.modelView.LoginMV;
//import com.example.app.helpClasses.ShowToastAndSignOut;
import com.example.app.modelView.helpClasses.ShowToastAndSignOut;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginModel {

    LoginMV loginMV;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private ShowToastAndSignOut showToastAndSignOut;


    public LoginModel(LoginMV loginController){
        this.loginMV = loginController;
        showToastAndSignOut = new ShowToastAndSignOut();
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }

    public void bCreate(String mail, String password) {

//        if(TextUtils.isEmpty(mail)|| TextUtils.isEmpty(password)){
//            loginController.showToast("Empty");
//            }
//
//        else if(password.length()<9){
//            loginController.showToast("you need more than 9 chars");
//        }
//        else{
//            notBlocked(mail,password);
//        }
    }

    // Check if the user not blocked
    public void notBlocked(String e_mail, String pass) {
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
                                loginMV.showToast("You are blocked");
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

    /**
     * Insert the user details to the database
     * Send him verification mail, without verify he cant enter the app!
     * @param txt_email
     * @param pas_email
     */
    private void regisuser(String txt_email, String pas_email) {
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
                    df.set(us_info);
                    loginMV.showToast("success, verify email");
                    loginMV.showStartActivity();

                }
                else{
                    loginMV.showToast("failed");
                }
            }
        });
    }

}
