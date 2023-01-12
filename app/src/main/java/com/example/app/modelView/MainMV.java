package com.example.app.modelView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.activities.AdminDetailActivity;
import com.example.app.activities.DetailActivity;
import com.example.app.activities.MainActivity;
import com.example.app.model.MainModel;
import com.example.app.model.objects.Category;
import com.example.app.model.objects.Location;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.SortType;
import com.example.app.modelView.adapters.ShapeAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class MainMV
{
    public static ArrayList<Site> shapeList;
    private MainActivity mainActivity;
    private MainModel mainModel;

    public MainMV(ArrayList<Site> shapeList, MainActivity mainActivity) {
        this.shapeList = shapeList;
        this.mainActivity = mainActivity;
//        this.initWidgets();
//        setContentView(R.layout.activity_main);
    }

    public void addModel(MainModel mainModel) {
        this.mainModel = mainModel;
    }

    public void is_admin(Site selectShape) {
        FirebaseFirestore fstore;
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String id = user.getUid();
        Log.d("MainActivity", "id = "+ id);
        fstore = FirebaseFirestore.getInstance();
        final boolean[] is_admin = new boolean[1];
        final Intent[] showDetail = new Intent[1];

        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("MainActivity", "in onComplete");
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    String is_us = doc.getString("isUser");
                    if(is_us.equals("0")){
                        Log.d("MainActivity", "is admin");
                        showDetail[0] = new Intent(mainActivity.getApplicationContext(), AdminDetailActivity.class);
                        Log.d("MainActivity", "ADMIN LOADED :)");
                    }
                    else {
                        Log.d("MainActivity", "is NOT admin");
                        showDetail[0] = new Intent(mainActivity.getApplicationContext(), DetailActivity.class);
                        Log.d("MainActivity", "admin NOT loaded");
                    }
                    showDetail[0].putExtra("id",selectShape.getId());
                    showDetail[0].putExtra("name",selectShape.getName());
                    mainActivity.startActivity(showDetail[0]);
                }
            }
        });
//                Log.d("MainActivity", "is admin = " + is_admin[0]);
//        return true; //is_admin[0];
    }

    public void checkForFilter(SortType type) {
            Log.d("checkForFilter-model", "selectedFilters = " + mainModel.getselectedFilters());
            if( mainModel.getselectedFilters().contains("all"))
            {
                mainModel.allFilterTappedForFlow(type);
            }
            else
            {
                mainModel.filterList(null, type);
            }
    }
}