package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.example.app.R;
import com.example.app.modelView.contactMV;
import com.google.android.material.textfield.TextInputLayout;

public class ContactActivity extends AppCompatActivity {
    private Button send;
    private TextInputLayout mes;
    private contactMV contactMV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        send = findViewById(R.id.send);
        mes = findViewById(R.id.Message);
        Editable text = mes.getEditText().getText();

        //Initialize MV of this activity
        contactMV = new contactMV(this);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = text.toString();
                contactMV.bSend(s);
            }
        });
    }
}