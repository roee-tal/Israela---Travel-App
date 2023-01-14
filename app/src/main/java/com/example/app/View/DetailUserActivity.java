package com.example.app.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.app.modelView.helpClassesMV.BottomNavActivity;
import com.example.app.R;
import com.example.app.modelView.DetailUserMV;


public class DetailUserActivity extends AppCompatActivity {

    private Button delete;
    private Button contact;
    private TextView email;

    DetailUserMV detailUserMV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        initializeXMLValues();

        //Initialize MV of this activity
        detailUserMV = new DetailUserMV(this);

        getSelectedShape();
        // For the menu bar in the bottom
        clickOnBottomNav();

        // Go to the chosen user messages
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToPass = email.getText().toString();
                // start the SecondActivity
                Intent intent = new Intent(new Intent(DetailUserActivity.this, ContactAdminActivity.class));
                intent.putExtra(Intent.EXTRA_TEXT, textToPass);
                detailUserMV.showContactAdminActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areYouSureMessage();
            }
        });
    }

    private void initializeXMLValues(){
        delete = findViewById(R.id.del);
        contact = findViewById(R.id.act);
        email = findViewById(R.id.Email);
    }

    // Dialog to check id the admin is sure to delete the user
    private void areYouSureMessage() {
        new AlertDialog.Builder(this).setMessage("Are you sure you want to delete?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent previousIntent = getIntent();
                        String parsedStringID = previousIntent.getStringExtra("id");

                        detailUserMV.bDelete(parsedStringID);
                    }
                })
                .setNegativeButton("No", null).show();
    }


    private void clickOnBottomNav() {
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);
    }


    // Show the chosen user email
    private void getSelectedShape() {
        Intent previousIntent = getIntent();
        String parsedStringID = previousIntent.getStringExtra("email");
        TextView siteName = (TextView) findViewById(R.id.Email);
        siteName.setText(parsedStringID);
    }





}

