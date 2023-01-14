package com.example.app.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app.modelView.AdminDetailsiteMV;
import com.example.app.model.helpClassesModel.AddRating;
import com.example.app.model.helpClassesModel.ImageLoad;
import com.example.app.R;
import com.example.app.modelView.adapters.ViewPagerAdapter;
import com.example.app.model.objects.GroupEvent;
import com.example.app.model.objects.Location;
import com.example.app.model.objects.Site;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminDetailActivity extends AppCompatActivity
{
    // creating object of ViewPager
    ViewPager mViewPager;

    // images array
    final ArrayList<Bitmap>  images = new ArrayList<Bitmap>();
    final ArrayList<Site>  siteSingalAtList = new ArrayList<Site>();


    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    String selectedShapeName;
    String selectedShapeID;

    //Create object of the modelView
    AdminDetailsiteMV adminDetailsiteMV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_detail);

        //Connection to modelView
        adminDetailsiteMV = new AdminDetailsiteMV(this,mViewPagerAdapter);

        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(AdminDetailActivity.this, images);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        getSelectedSite();

    }

    // Get the relevant data site from the previous Intent

    private void getSelectedSite()
    {
        Intent previousIntent = getIntent();
        selectedShapeID = previousIntent.getStringExtra("id");
        selectedShapeName = previousIntent.getStringExtra("name");
        Log.d("updateSelectedName", "name="+selectedShapeName);
        Log.d("updateSelectedName", "id="+selectedShapeID);
        getParsedSite(selectedShapeID);
    }


    private void getParsedSite(String parsedID)
    {
        adminDetailsiteMV.getParsedShapee(parsedID);
        adminDetailsiteMV.updateImageDetailList(parsedID,images,mViewPagerAdapter);
    }

    /**
     * when click on add rating button. Moves to AddRating class
     * @param view
     */
    public void addRatingTapped(View view){
        Intent AddRate = new Intent(getApplicationContext(), AddRating.class);
        AddRate.putExtra("id",selectedShapeID);
        startActivity(AddRate);
    }

    /**
     * when click on edit name button
     * @param view
     */
    public void nameEditButtonClicked(View view){
        // Builds the dialog with the messages
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Enter the new name:");
        final EditText input = new EditText(this);
        builder.setView(input);
        //When click on update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                adminDetailsiteMV.bUpdateName(selectedShapeID,input);
            }
        });

        //When click on cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * when click on edit rating button
     * @param view
     */
    public void rateEditButtonClicked(View view){
        // Builds the dialog with the RatingBar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");

        final RatingBar generalRate = new RatingBar(this);
        generalRate.setMax(5);
        final TextView textGeneralRate = new TextView(this);
        textGeneralRate.setText("Enter the new rate:");
        //Design Dialog screeen
        generateDialogScreeen(builder,generalRate,textGeneralRate);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                adminDetailsiteMV.bUpdateRate(selectedShapeID,generalRate);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void generateDialogScreeen(AlertDialog.Builder builder, RatingBar generalRate, TextView textGeneralRate) {
        generalRate.setNumStars(5);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textGeneralRate);
        layout.addView(generalRate);
        builder.setView(layout);
    }


    /**
     * when click on edit location button
     * @param view
     */
    public void locationEditButtonClicked(View view){
        // Builds the dialog with the RatingBar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Select the new location:");

        LinearLayout layout = new LinearLayout(this);
        final String[] options = {Location.North.name(), Location.Center.name(), Location.South.name()};
        // Choose the location by spinner
        final Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layout.addView(spinner);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                adminDetailsiteMV.bUpdateLocation(selectedShapeID,spinner);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * when click on edit detail button
     * @param view
     */
    public void detailEditButtonClicked(View view){
        // Builds the dialog with the messages
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Enter the new detail:");
        final EditText input = new EditText(this);
        builder.setView(input);
        //When click on update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                adminDetailsiteMV.bUpdateDetails(selectedShapeID,input);
            }
        });
        //When click on cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * when click on edit detail button
     * @param view
     */
    public void addEventButtonClicked(View view){
        // Builds the dialog with the messages
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        final EditText inputDate = new EditText(this);
        final EditText inputDetail = new EditText(this);
        final TextView textdetail = new TextView(this);
        final TextView textDate = new TextView(this);
        textDate.setText("Enter the date of the event:");
        textdetail.setText("Enter meeting detail:");
        LinearLayout layout = new LinearLayout(this);
        setLayout(layout,textDate,inputDate,textdetail,inputDetail);
        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                adminDetailsiteMV.bAddEvent(selectedShapeID,inputDate,inputDetail);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Help func
    private void setLayout(LinearLayout layout, TextView textDate, EditText inputDate, TextView textdetail, EditText inputDetail) {
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textDate);
        layout.addView(inputDate);
        layout.addView(textdetail);
        layout.addView(inputDetail);
    }

    // When click on load image button
    public void loadImageButtonClicked(View view){
        Intent intent = new Intent(AdminDetailActivity.this, ImageLoad.class);
        intent.putExtra("siteID", selectedShapeID);
        startActivity(intent);
    }

    // Help function to declare vars
    public void initVal(Site s) {
        TextView siteName = (TextView) findViewById(R.id.siteName);
        TextView siteDetail = (TextView) findViewById(R.id.siteDetail);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating((float) s.getRate());
        LinearLayout tagContainer = findViewById(R.id.tags);
        adminDetailsiteMV.setVals(s,siteName,siteDetail,tagContainer);
    }

    // Set design
    public void setText(TextView tagData, LinearLayout tagContainer, String tag, boolean is_new) {
        tagData.setText(tag);
        if (!is_new)
            return; //already has a parent
        ViewGroup.MarginLayoutParams params = new ActionMenuView.LayoutParams(ActionMenuView.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 8;
        tagData.setLayoutParams(params);
        tagData.setBackgroundResource(R.drawable.tag_box_background);
        tagData.setPadding(20, 20, 20, 20);
        tagData.setTextSize(20);
        tagData.setText(tag);
        tagContainer.addView(tagData);
    }

    //Set the values
    public void setVal(TextView siteName, TextView siteDetail, Site s) {
        String detail = s.getDetail();
        siteName.setText(s.getName());
        siteDetail.setText(detail);
    }
}