package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    private Button usersList;
    private Button messages;
    private Button signOut;
    private Button Users;
    private Button places;
    private TextView admin;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth auth;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        usersList = findViewById(R.id.users_list);
        messages = findViewById(R.id.messages);
        admin = findViewById(R.id.nam);

        String mail = user.getEmail();
        admin.setText(mail);



        clickOnBottomNav();

        usersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, UsersActivity.class));
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, MainActivity.class));

            }
        });

    }

    private void clickOnBottomNav(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        nav = findViewById(R.id.bottom_nav_admi);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sign_out_admin:
                        areYouSureMessage();
                        break;

                    case R.id.u:
                        startActivity(new Intent(AdminActivity.this, UsersActivity.class));
                        break;

                    case R.id.p:
                        startActivity(new Intent(AdminActivity.this, MainActivity.class));
                        break;

                }
                return true;
            }
        });
    }
    private void areYouSureMessage(){
        new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                })
                .setNegativeButton("No",null).show();
    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {

                    finish();
                    startActivity(new Intent(AdminActivity.this, StartActivity.class));
                }
                else {
                    startActivity(new Intent(AdminActivity.this, StartActivity.class));
                }
            }

        });



    }
}