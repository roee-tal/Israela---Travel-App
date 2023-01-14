package com.example.app.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.modelView.DetailSiteMV;
import com.example.app.modelView.adapters.ViewPagerAdapter;
import com.example.app.model.objects.Review;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity
{

    private Button eventGroup;
    private AlertDialog.Builder builder;

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
    DetailSiteMV detailSiteMV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Connection to modelView
        detailSiteMV = new DetailSiteMV(this,mViewPagerAdapter);

        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);
        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, images);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        getSelectedSite();

        eventGroup = findViewById(R.id.event);

        loadEvent();
    }

    // Get the relevant data site from the previous Intent
    private void getSelectedSite(){
        Intent previousIntent = getIntent();
        selectedShapeID = previousIntent.getStringExtra("id");
        selectedShapeName = previousIntent.getStringExtra("name");
        getParsedSite(selectedShapeID);
    }


    private void getParsedSite(String parsedID)
    {
        detailSiteMV.getParsedShapee(parsedID);
        detailSiteMV.updateImageDetailList(parsedID,images,mViewPagerAdapter);
    }

    /**
     * When click on 'add rating button'
     * Build dialog with the rating bar
     * @param view
     */
    public void addRatingTapped(View view){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.rating_add, null);
        mBuilder.setView(mView);

        final RatingBar mainRateBar = (RatingBar) mView.findViewById(R.id.mainRateBar);

        Button submitButton = (Button) mView.findViewById(R.id.submitButton);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        // When click on 'submit'
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailSiteMV.bSubmit(mainRateBar,dialog,selectedShapeID);

            }
        });
    }

    public void showGroupEvent(View view){
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadEvent(){
        loadEvent(selectedShapeID);
    }

    public void loadEvent(String shapeID){
        builder = new AlertDialog.Builder(this);
        detailSiteMV.LoadEvent(shapeID);

        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                detailSiteMV.whenJoinGroup(selectedShapeID);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    /**
     * When click on 'add review'
     * Build dialog
     * @param view
     */
    public void addReviewTapped(View view)  {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_add_review, null);
        //Show the name
        final EditText mName = (EditText) mView.findViewById(R.id.name);
        //Show the review
        final EditText mReview = (EditText) mView.findViewById(R.id.review);
        Button mAdd = (Button) mView.findViewById(R.id.add);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //When click on 'add review'
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String review = mReview.getText().toString();
                detailSiteMV.addReview(name,review,dialog);

            }
        });
    }

    /**
     * When click on 'show reviews'
     * Build dialog with all reviews
     * @param view
     */
    public void showReviewsTapped(View view) {

        //Creates the review list with adapter
        ArrayList<String> arrayListReviews = new ArrayList<>();
        ArrayAdapter<String> adapterReview = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListReviews);
        detailSiteMV.showReviews(adapterReview,arrayListReviews);

        // Show the reviews
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.reviews_list_view, null);
        alertDialog.setView(convertView);

        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        lv.setAdapter(adapterReview);

        alertDialog.show();
    }

    // Help function to declare vars of the site
    public void initVal(Site s) {
        TextView siteName = (TextView) findViewById(R.id.siteName);
        TextView siteDetail = (TextView) findViewById(R.id.siteDetail);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setRating((float) s.getRate());
        LinearLayout tagContainer = findViewById(R.id.tags);
        detailSiteMV.setVals(s,siteName,siteDetail,tagContainer);
    }

    // design the data
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
        if (s.getEvent() != null)
            eventGroup.setVisibility(View.VISIBLE);
        else
            eventGroup.setVisibility(View.GONE);

    }

    public void updateTripDetails(Site s) {
        if (s.getEvent() != null) {

                    builder.setTitle("Group Event");
                    builder.setMessage("Date: " + s.getEvent().getDateEvent() +
                            "\nParticipants: " + s.getEvent().getPeopleEvent() +
                            "\nDetail: " + s.getEvent().getMeetingDetail());
                    final EditText input = new EditText(DetailActivity.this);

                }
    }

    /**
     * When joining to group trip
     */
    public void welcomeMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        builder.setTitle("Welcome");
                        builder.setMessage("Welcome to our group trip!\n" +
                                "If you you want to see detail about the trip,\n" +
                                "pleas click again on the detail event button." +
                                "\nFor more info you can contact us.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do something when the "OK" button is clicked
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
    }
}