package com.example.app.modelView;

import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.View.AdminDetailActivity;
import com.example.app.View.DetailActivity;
import com.example.app.View.MainActivity;
import com.example.app.model.MainModel;
import com.example.app.model.objects.Category;
import com.example.app.model.objects.Location;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.SortType;
import com.example.app.modelView.adapters.ShapeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainMV
{
    public static ArrayList<Site> shapeList;
    private MainActivity mainActivity;
    private MainModel mainModel;

    public MainMV(ArrayList<Site> shapeList, MainActivity mainActivity, ShapeAdapter adapter) {
        this.shapeList = shapeList;
        this.mainActivity = mainActivity;
        mainModel = new MainModel(shapeList,this,adapter);
//        this.initWidgets();
//        setContentView(R.layout.activity_main);
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

    public void filterList(String s, Object o) {
        mainModel.filterList(s, null);
    }

    public void allFilterTapped() {
        mainModel.allFilterTapped();
    }

    public void setupData() {
        mainModel.setupData();
    }

    public boolean loadByText(String str) {
        return mainModel.loadByText(str);
    }

    public void setAdapter(ShapeAdapter adapter) {
        mainModel.setAdapter(adapter);
    }

    public void allFilterTappedForFlow(SortType rateUp2Down) {
        mainModel.allFilterTappedForFlow(rateUp2Down);
    }

    public void checkIfUser() {

        mainModel.showMatchingAuth();
    }

    public void applyUser() {
        mainActivity.applyUser();
    }

    public void applyAdmin() {
        mainActivity.applyAdmin();
    }

    public void popUpEvents(MainActivity mainActivity) {
        mainModel.popUpEvents(mainActivity);
    }

    public void getTheEvent(ArrayAdapter<String> adapterEvent) {
        mainActivity.getTheEvent(adapterEvent);
    }

    public void showToast(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

    public void bAdd(EditText mName, EditText mRate, EditText mShadeRate, EditText mDetails, EditText mLocation, EditText mCategory) {
        String name = mName.getText().toString();
        String rate = mRate.getText().toString();
        String shade_rate = mShadeRate.getText().toString();
        String details = mDetails.getText().toString();
        String location = mLocation.getText().toString();
        String category = mCategory.getText().toString();
        Location currLocation = Location.Center;
        Category currCategory = Category.track;

        if (location.equals("center")) currLocation = Location.Center;
        else if (location.equals("north")) currLocation = Location.North;
        else if  (location.equals("south")) currLocation = Location.South;
        else location = "";

        if (category.equals("track")) currCategory = Category.track;
        else if (category.equals("picnic")) currCategory = Category.picnic;
        else if  (category.equals("swimming")) currCategory = Category.swimming;
        else category = "";

        if ( name.isEmpty() || rate.isEmpty() ||
                shade_rate.isEmpty() || details.isEmpty() || location.isEmpty() || category.isEmpty()) {
            showToast("Please fill in all fields");

        }else{


            Site newSite = new Site("13",name, Integer.parseInt(rate), details, currLocation, Integer.parseInt(shade_rate),1,1, null, currCategory);

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("site").push().setValue(newSite);
            showToast("success");
        }
    }
}
