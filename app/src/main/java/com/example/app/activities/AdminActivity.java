package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.app.helpClasses.BottomNavActivity;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {

    private Button usersList;
    private Button messages;
    private TextView admin;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initializeXMLValues();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String mail = user.getEmail();
        admin.setText(mail);

        //For Menu bar in the bottom
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

    private void initializeXMLValues(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        usersList = findViewById(R.id.users_list);
        messages = findViewById(R.id.messages);
        admin = findViewById(R.id.nam);
    }

    private void clickOnBottomNav(){
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);
    }
}