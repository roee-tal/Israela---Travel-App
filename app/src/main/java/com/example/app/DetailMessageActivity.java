package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.controller.DetailMessageController;
import com.example.app.controller.ShowToastAndSignOut;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailMessageActivity extends AppCompatActivity {
    private Button delete;
//    private FirebaseFirestore fstore;
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
    private DetailMessageController detailMessageController;
//    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

        getSelectedShape();

        delete = findViewById(R.id.del);
//        fstore = FirebaseFirestore.getInstance();
        detailMessageController = new DetailMessageController(this);

        clickOnBottomNav();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent previousIntent = getIntent();
                String parsedStringID = previousIntent.getStringExtra("id");
                detailMessageController.bDelete(parsedStringID);
//                deleteAccount(parsedStringID);
            }
        });
    }

    private void clickOnBottomNav(){
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this,gso);
//        nav = findViewById(R.id.bottom_nav_admi);
//        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.sign_out_admin:
//                        areYouSureMessageToExit(gsc);
//                        break;
//
//                    case R.id.u:
//                        startActivity(new Intent(DetailMessageActivity.this, UsersActivity.class));
//                        break;
//
//                    case R.id.p:
//                        startActivity(new Intent(DetailMessageActivity.this, MainActivity.class));
//                        break;
//
//                }
//                return true;
//            }
//        });
    }

    public void areYouSureMessage(String parsedMail, String id){
        new AlertDialog.Builder(this).setMessage("Are you sure you want to delete the message?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        detailMessageController.bAreYouSureLogic(DetailMessageActivity.this,parsedMail,id);
//                        del(parsedMail);
//                        Toast.makeText(DetailMessageActivity.this, "message removed!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(DetailMessageActivity.this, UsersActivity.class));
//                        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    DocumentSnapshot document = task.getResult();
//                                    String s = document.getString("LettersNum");
//                                    int num = Integer.parseInt(s);
//                                    num--;
//                                    s = Integer.toString(num);
//                                    fstore.collection("Users").document(id).update("LettersNum", s);
//                                }
//                            }
//                        });
                    }
                })
                .setNegativeButton("No",null).show();
    }

//    private void areYouSureMessageToExit(GoogleSignInClient gsc){
//        new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?").
//                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ShowToastAndSignOut showToastAndSignOut = new ShowToastAndSignOut();
//                        showToastAndSignOut.signOut(gsc,DetailMessageActivity.this);
//                        signOut();
//                    }
//                })
//                .setNegativeButton("No",null).show();
//    }
//
//    void signOut(){
//        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(Task<Void> task) {
//                if (task.isSuccessful()) {
//
//                    finish();
//                    startActivity(new Intent(DetailMessageActivity.this, StartActivity.class));
//                }
//                else {
//                    startActivity(new Intent(DetailMessageActivity.this, StartActivity.class));
//                }
//            }
//
//        });
//    }

//    void del(String parsedMail){
//        fstore.collection("Messages").document(parsedMail).delete();
//    }

    private void getSelectedShape(){

        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String mail = intent.getStringExtra("mail");
        TextView textView = (TextView) findViewById(R.id.mes);
        TextView mailview = (TextView) findViewById(R.id.email_cont);
        textView.setText(text);
        mailview.setText(mail);
    }

//    private void deleteAccount(String parsedMail){
//        Log.d("ASSD",parsedMail);
//        fstore.collection("Messages").document(parsedMail)
//        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    String uId = document.getString("ID");
//                    String uuId = document.getString("mesId");
//                    Log.d("String uIdNamess", "id="+uId);
//                    areYouSureMessage(parsedMail,uId);
//                }
//            }
//        });
//    }
}