package com.example.app.model;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import androidx.annotation.NonNull;

import com.example.app.AdminActivity;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.StartActivity;
import com.example.app.controller.startController;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class startModel extends AppCompatActivity {

        private startController controller;

        private FirebaseAuth auth;
        private FirebaseFirestore fstore;
        GoogleSignInOptions gso;
        GoogleSignInClient gsc;


        public startModel(startController controller){
            this.controller = controller;
        }



        public void bLoginMail(String mail, String password) {
//                String txt_email = email.getText().toString();
//                String pas_email = password.getText().toString();

                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)){
//                    Toast.makeText(context, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    notBlocked(mail, password);
//                        loginu(mail, password);
//                    startActivity(new Intent(StartActivity.this, MainActivity.class)); // Todo: disable this to skip authentication phase - Debug Mode
//                    finish();
                }
        }

        private void notBlocked(String e_mail, String pass) {

            auth = FirebaseAuth.getInstance();
            fstore = FirebaseFirestore.getInstance();
            fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
                  .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                              QuerySnapshot querySnapshot = task.getResult();
                                  int size = querySnapshot.size();
                                  if(size != 0) {
                                      auth.signOut();
                                      Toast.makeText(startModel.this, "You are blocked", Toast.LENGTH_LONG).show();
                                  }
                                  else{
                                       loginu(e_mail, pass);
                                  }
                        }
                                  else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                        }
                                }
                        });
        }

    private void loginu(String txt_email, String pas_email) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    assert user != null;
                    if(user.isEmailVerified()) {
                        controller.showToast("welcome");
//                        Toast.makeText(StartActivity.this, "welcome", Toast.LENGTH_LONG).show();
                        checkUserAccessLevel(user.getUid());
//                        finish();
                    }
                    else{
                        controller.showToast("Please verify your email");
//                        Toast.makeText(StartActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    controller.showToast("Wrong password");
                }
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        fstore = FirebaseFirestore.getInstance();
        DocumentReference df =  fstore.collection("Users").document(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        if (document.getString("isUser").equals("1")) {
                            controller.showUserActivity();
                        }
                        else{
                            controller.showAdminActivity();
                        }
                    }
                    else{
                        controller.showToast("failed");
                    }
                }
            }
        });
    }

    public void bLoginG(Activity activity) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(activity.getString(R.string.default_web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(activity, gso);

        Log.d("GGG1", gso.toString());
        Log.d("GGG2", gsc.toString());
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken
//                (getString(R.string.default_web_client_id)).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        Intent signInIntent = gsc.getSignInIntent();
        activity.startActivityForResult(signInIntent,1000);

    }


    public void activityRes(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String mail = account.getEmail();
                notBlockedG(mail, account);

            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void notBlockedG(String e_mail, GoogleSignInAccount account) {

        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            int size = querySnapshot.size();
                            if(size != 0) {
                                auth.signOut();
                                Toast.makeText(startModel.this, "You are blocked", Toast.LENGTH_LONG).show();
                            }
                                else {
                                    firebaseauthwithgoogle(account);
                                }
                            }

                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void firebaseauthwithgoogle(GoogleSignInAccount account) {
        AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(cred).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = auth.getCurrentUser();
                String uid = user.getUid();
                String e_mail = user.getEmail();
                if (authResult.getAdditionalUserInfo().isNewUser()) {
//                    if(notBlocked(e_mail)) {
                    DocumentReference df = fstore.collection("Users").document(user.getUid());
                    Map<String, Object> us_info = new HashMap<>();
                    us_info.put("ID", uid);
                    us_info.put("Email", e_mail);
                    us_info.put("isUser", "1");
                    us_info.put("LettersNum", "0");
                    us_info.put("isBlocked", false);
                    df.set(us_info);
                    FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(us_info);
                    controller.showToast("account created");

//                    Toast.makeText(getApplicationContext(), "account created", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "You are blocked", Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    controller.showToast("Welcome");

//                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                }
                checkUserAccessLevel(uid);
            }
        });
    }


}
