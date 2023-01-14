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
import com.example.app.modelView.DetailMessageMV;

public class DetailMessageActivity extends AppCompatActivity {
    private Button delete;
    private DetailMessageMV detailMessageMV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

        getSelectedShape();
        //Initialize MV of this activity
        detailMessageMV = new DetailMessageMV(this);

        // For the menu bar in the bottom
        clickOnBottomNav();

        delete = findViewById(R.id.del);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent previousIntent = getIntent();
                String parsedStringID = previousIntent.getStringExtra("id");
                detailMessageMV.bDelete(parsedStringID);
//                deleteAccount(parsedStringID);
            }
        });
    }

    private void clickOnBottomNav(){
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);
    }

    // Dialog to make sure the admin wants to delete the message
    public void areYouSureMessage(String parsedMail, String id){
        new AlertDialog.Builder(this).setMessage("Are you sure you want to delete the message?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        detailMessageMV.bAreYouSureLogic(DetailMessageActivity.this,parsedMail,id);
                    }
                })
                .setNegativeButton("No",null).show();
    }

    /**
     * Get the mail and the message that selected in the previous intent
     * The mail we get is a direct link to the user!
     */
    private void getSelectedShape(){
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String mail = intent.getStringExtra("mail");
        TextView textView = (TextView) findViewById(R.id.mes);
        TextView mailview = (TextView) findViewById(R.id.email_cont);
        textView.setText(text);
        mailview.setText(mail);
    }
}