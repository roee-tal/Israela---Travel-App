package com.example.app.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.app.R;
import com.example.app.modelView.LoginMV;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText user;
    private Button create;
    private LoginMV loginMV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeXMLValues();

        //Initialize MV of this activity
        loginMV = new LoginMV(this);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginMV.bCreate(email.getText().toString(),password.getText().toString());

            }
        });
    }

    private void initializeXMLValues(){
        email = findViewById(R.id.email);
        create = findViewById(R.id.create);
        password = findViewById(R.id.password);
        user = findViewById(R.id.Username);
    }


}

