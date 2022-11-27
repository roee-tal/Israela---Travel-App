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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button create;
    private Button login;

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
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
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
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
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(pas_email)){
                    Toast.makeText(StartActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    loginu(txt_email , pas_email);
                }
            }
        });

//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager.getInstance().logInWithReadPermissions(StartActivity.this, Arrays.asList("public_profile"));
//            }
//        });
    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseauthwithgoogle(account);
//                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//                task.getResult(ApiException.class);
//                FirebaseUser fu = auth.getCurrentUser();
//                DocumentReference df = fstore.collection("Users").document(fu.getUid());
//                Map<String,Object> us_info = new HashMap<>();
//                us_info.put("FullName", personName.getText().toString());
//                us_info.put("Email", email.getText().toString());
//                df.set(us_info);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }

//    private void firebaseauthwithgoogle(GoogleSignInAccount account) {
//        AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(),null);
//        auth.signInWithCredential(cred).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    FirebaseUser user = auth.getCurrentUser();
//                DocumentReference df = fstore.collection("Users").document(user.getUid());
//                Map<String,Object> us_info = new HashMap<>();
//                email.setText(account.getEmail());
//                us_info.put("Email", email.toString());
//                df.set(us_info);
//                }
//            }
//        });
//    }

    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(StartActivity.this,SecActivity.class);
        startActivity(intent);
    }




    private void loginu(String txt_email, String pas_email) {
        auth.signInWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if(user.isEmailVerified()) {
                        Toast.makeText(StartActivity.this, "success", Toast.LENGTH_LONG).show();
                        checkUserAccessLevel(user.getUid());
                        finish();
                    }
                    else{
                        Toast.makeText(StartActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                    }
                }
            }
//        auth.signInWithEmailAndPassword(txt_email, pas_email).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                Toast.makeText(StartActivity.this, "successful", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(StartActivity.this, MainActivity.class));
//                finish();
//            }

        });


    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df =  fstore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess" + documentSnapshot.getData());
                if(documentSnapshot.getString("isAdmin")!=null){
                    startActivity(new Intent(StartActivity.this, AdminActivity.class));
                    finish();
                }
                if(documentSnapshot.getString("isUser")!=null){
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}

