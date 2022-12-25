package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContactAdminActivity extends AppCompatActivity
{
    // creating object of ViewPager

    // images array
    public static ArrayList<Message> messList = new ArrayList<Message>();
    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private ListView listView;
    private TextView email;
    private Button contact;
    private BottomNavigationView nav;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth auth;
    private MessageAdapter adapter;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_admin);
        email = findViewById(R.id.email_con);
        fstore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();




        clickOnBottomNav();
        getSelectedShape();
        setUpList();

        setUpOnclickListener();
        selectedFilters.add("all");

    }


    private void clickOnBottomNav(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        nav = findViewById(R.id.bottom_nav_admin);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sign_out_admin:
                        areYouSureMessage();
                        break;

                    case R.id.u:
                        startActivity(new Intent(ContactAdminActivity.this, UsersActivity.class));
                        break;

                    case R.id.p:
                        startActivity(new Intent(ContactAdminActivity.this, MainActivity.class));
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
                    startActivity(new Intent(ContactAdminActivity.this, StartActivity.class));
                }
                else {
                    startActivity(new Intent(ContactAdminActivity.this, StartActivity.class));
                }
            }

        });
    }

    private void getSelectedShape()
    {
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        // use the text in a TextView
        TextView textView = (TextView) findViewById(R.id.email_con);
        textView.setText(text);
        Log.d("updateSelectedName", "id="+text);
        setupData(text);
    }

    private void setupData(String text)
    {
        fstore = FirebaseFirestore.getInstance();
        messList.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        fstore.collection("Messages").whereEqualTo("Email",text)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            messList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String mess = document.getString("Message");
                                String id = document.getString("mesId");
                                Message m = new Message(mess,id);
                                Log.d("TAGGGG", m.getText());
                                Log.d("TAGGGGID", m.getid());
                                messList.add(m);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void setUpList()
    {
        listView = (ListView) findViewById(R.id.shapesListView);

        setAdapter(messList);
    }

    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                String s = email.getText().toString();
                Message selectMess = (Message) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailMessageActivity.class);
                showDetail.putExtra(Intent.EXTRA_TEXT,selectMess.getText());
                showDetail.putExtra("mail",s);
                showDetail.putExtra("id",selectMess.getid());
                startActivity(showDetail);
                finish();
            }
        });
    }

    public void messageTapped(View view)
    {
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        setupData(text);
    }


    private void setAdapter(ArrayList<Message> messList)
    {
        adapter = new MessageAdapter(getApplicationContext(), 0, messList);
        listView.setAdapter(adapter);
    }
}
