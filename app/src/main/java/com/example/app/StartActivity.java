package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.controller.startController;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;


import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class StartActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button create;
    private Button login;

    private startController controller;
//    private FirebaseAuth auth;
//    private FirebaseFirestore fstore;
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
    ImageView googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        create = findViewById(R.id.create);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        googleBtn = findViewById(R.id.faceb);

        controller = new startController(this);
//        auth = FirebaseAuth.getInstance();
//        fstore = FirebaseFirestore.getInstance();
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
//                requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signIn();
//                Log.d("GGG1", gso.toString());
//                Log.d("GGG2", gsc.toString());
                controller.bLoginG(StartActivity.this);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
                String pas_email = password.getText().toString();
                controller.bLoginMail(txt_email, pas_email);
//                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(pas_email)){
//                    Toast.makeText(StartActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
//                } else {
//                        notBlocked(txt_email, pas_email);
//                        loginu(txt_email, pas_email);
//                    startActivity(new Intent(StartActivity.this, MainActivity.class)); // Todo: disable this to skip authentication phase - Debug Mode
//                    finish();
//                }
            }
        });
    }

//    void signIn(){
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent,1000);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if(requestCode == 1000){
        controller.activityRes(requestCode, resultCode, data);
            //            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                String mail = account.getEmail();
//                notBlockedG(mail, account);
//
//            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
        }
    }

//    private void notBlockedG(String e_mail, GoogleSignInAccount account) {
//        fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            QuerySnapshot querySnapshot = task.getResult();
//                            int size = querySnapshot.size();
//                            if(size != 0) {
//                                auth.signOut();
//                                gsc.signOut();
//                                Toast.makeText(StartActivity.this, "You are blocked", Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                firebaseauthwithgoogle(account);
//                            }
//                        }
//                        else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }


//    private void firebaseauthwithgoogle(GoogleSignInAccount account) {
//        AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        auth.signInWithCredential(cred).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                FirebaseUser user = auth.getCurrentUser();
//                String uid = user.getUid();
//                String e_mail = user.getEmail();
//                if (authResult.getAdditionalUserInfo().isNewUser()) {
////                    if(notBlocked(e_mail)) {
//                        DocumentReference df = fstore.collection("Users").document(user.getUid());
//                        Map<String, Object> us_info = new HashMap<>();
//                        us_info.put("ID", uid);
//                        us_info.put("Email", e_mail);
//                        us_info.put("isUser", "1");
//                        df.set(us_info);
//                        FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(us_info);
//                        Toast.makeText(getApplicationContext(), "account created", Toast.LENGTH_SHORT).show();
////                    }
////                    else {
////                        Toast.makeText(getApplicationContext(), "You are blocked", Toast.LENGTH_SHORT).show();
////                    }
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
//                }
//                checkUserAccessLevel(uid);
//            }
//        });
//    }

//    private void notBlocked(String e_mail,String pass) {
//
//        fstore.collection("BlockedUsers").whereEqualTo("email",e_mail)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            QuerySnapshot querySnapshot = task.getResult();
//                            int size = querySnapshot.size();
//                            if(size != 0) {
//                                auth.signOut();
//                                Toast.makeText(StartActivity.this, "You are blocked", Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                loginu(e_mail, pass);
//                            }
//                            }
//                         else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }

//    private void loginu(String txt_email, String pas_email) {
//        auth.signInWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser user = auth.getCurrentUser();
//                    assert user != null;
//                    if(user.isEmailVerified()) {
//                        Toast.makeText(StartActivity.this, "welcome", Toast.LENGTH_LONG).show();
//                        checkUserAccessLevel(user.getUid());
//                        finish();
//                    }
//                    else{
//                        Toast.makeText(StartActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
//    }

//    private void checkUserAccessLevel(String uid) {
//        DocumentReference df =  fstore.collection("Users").document(uid);
//        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                if (document.exists()) {
//                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
//                        if (document.getString("isUser").equals("1")) {
//                            startActivity(new Intent(StartActivity.this, MainActivity.class));
//                            finish();
//                        }
//                        else{
//                            startActivity(new Intent(StartActivity.this, AdminActivity.class));
//                            finish();
//                        }
//                    }
//                else{
//                    Toast.makeText(StartActivity.this, "failed", Toast.LENGTH_LONG).show();
//
//                }
//                }
//            }
//        });
//    }
}

