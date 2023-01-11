package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.app.R;
import com.example.app.modelView.startMV;


public class StartActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button create;
    private Button login;

    private startMV startMv;
    ImageView googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initializeXMLValues();

        //Initialize MV of this activity
        startMv = new startMV(this);


        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMv.bLoginG(StartActivity.this);
            }
        });

        // Create account with mail and password
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMv.showLoginActivity();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
                String pas_email = password.getText().toString();
                startMv.bLoginMail(txt_email, pas_email);

            }
        });
    }

    private void initializeXMLValues(){
        create = findViewById(R.id.create);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        googleBtn = findViewById(R.id.faceb);
    }


    //this func is for google sign-in
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data); if(requestCode == 1000){
            startMv.activityRes(requestCode, resultCode, data);

        }
    }
}

