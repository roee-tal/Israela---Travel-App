package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
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
//            {R.drawable.facebook, R.drawable.pic, R.drawable.pic2, R.drawable.facebook,
//            R.drawable.pic, R.drawable.pic2, R.drawable.facebook, R.drawable.pic};

    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    Site selectedShape;
    String selectedShapeName;
    String selectedShapeID;
    private StorageReference myStorage;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_detail);
        getSelectedShape();
//        updateSelectedName();
        this.updateImageDetailList(selectedShapeID);

//        setValues();

//        setContentView(R.layout.activity_detail);

        //-------------- first page --------------------
        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(AdminDetailActivity.this, images);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

    }

    private void getSelectedShape()
    {
        Intent previousIntent = getIntent();
        selectedShapeID = previousIntent.getStringExtra("id");
        selectedShapeName = previousIntent.getStringExtra("name");
        Log.d("updateSelectedName", "name="+selectedShapeName);
        Log.d("updateSelectedName", "id="+selectedShapeID);
        getParsedShape(selectedShapeID);
    }

    private void getParsedShape(String parsedID)
    {
//        final ArrayList<Site> siteList = new ArrayList<Site>();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("site").orderByChild("id").equalTo(parsedID).addValueEventListener(new ValueEventListener() {
            /**
             * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                siteSingalAtList.clear();
                Log.d("getParsedShape", "empty:"+siteSingalAtList.size());
                for (DataSnapshot child : children) {
                    Site s = child.getValue(Site.class);
                    siteSingalAtList.add(s);
                    this.setValues(s);
                    Log.d("getParsedShape", "siteSingalAtList.size="+siteSingalAtList.size());
                    assert s != null;
                }
//                mViewPagerAdapter.notifyDataSetChanged();
                Log.d("getParsedShape", "siteSingalAtList.size="+siteSingalAtList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            private void setValues(Site s)
            {
                TextView siteName = (TextView) findViewById(R.id.siteName);
                TextView siteDetail = (TextView) findViewById(R.id.siteDetail);
//        ImageView iv = (ImageView) findViewById(R.id.mainImage);
                String detail = "Name: " + s.getName() +
                        "\n\nArea: " + s.getLocation() +
                        "\n\nRate: " + s.getRate() + "/5" +
                        "\n\nShade Rate: " + s.getShadeRate() + "/5" +
                        "\n\nDetail: " + s.getDetail();
                siteName.setText(s.getName());
                siteDetail.setText(detail);
//        setImageList();

//        iv.setImageResource(selectedShape.getImage()); //Todo: do all image like this
            }});
        //--------------------------------------------
////        shapeList.clear(); //only for self check of data loading
//        for (Site site:siteList) {
////            shapeList.add(site);
//            Log.d("firebase0129", site.getRate() +"---"+ site.getName());
//        }


//
//        for (Site site : MainActivity.shapeList)
//        {
//            if(site.getId().equals(parsedID))
//                return site;
//        }
//        return null;
    }



    private void updateImageDetailList(String shapeID) {
//        shapeName = "Jerusalem Forest";
        Log.d("updateImageDetailList", "shapeName="+shapeID);
        myStorage = FirebaseStorage.getInstance().getReference().child("picture/"+shapeID);//+shapeName
        try {
            final File localTempFile = File.createTempFile("shvil", "jpg");

            myStorage.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                              @Override
                                              public void onSuccess(ListResult listResult) {

                                                  for(StorageReference file:listResult.getItems())
                                                  {
                                                      Log.d("firebaseFailed", "file.getName="+file.getName());
                                                      file.getFile(localTempFile).addOnSuccessListener((new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                          @Override
                                                          public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                                              Toast.makeText(AdminDetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
                                                              Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
                                                              images.add(bitmap);
                                                              Log.d("firebaseFailed", "images.size="+images.size());
                                                              mViewPagerAdapter.notifyDataSetChanged();
                                                          }
                                                      })).addOnFailureListener(new OnFailureListener() {
                                                          @Override
                                                          public void onFailure(@NonNull Exception e) {
                                                              Log.e("firebaseFailed", e.getMessage());
                                                          }
                                                      });
                                                  }
                                              }
                                          }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("firebaseFailed", "error!");
                            Toast.makeText(AdminDetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addRatingTapped(View view){
        Intent AddRate = new Intent(getApplicationContext(), AddRating.class);
        AddRate.putExtra("id",selectedShapeID);
        AddRate.putExtra("name",selectedShapeName);
        startActivity(AddRate);
//        startActivity(new Intent(DetailActivity.this, AddRating.class)); // Todo: disable this to skip authentication phase - Debug Mode
//        finish();
    }

    public void nameEditButtonClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Enter the new name:");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
                            String objectDB_Id = child.getKey();
                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
                            Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                            this.updateSite(s, objectDB_Id);
                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
                            assert s != null;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    private void updateSite(Site s, String objectDB_Id)
                    {
                        // Update data in Firebase
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", input.getText().toString());
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
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

    public void rateEditButtonClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
//        builder.setMessage("Enter the new shade rate:");
        final RatingBar generalRate = new RatingBar(this);
        generalRate.setMax(5);
        final RatingBar shadeRate = new RatingBar(this);
        final TextView textShade = new TextView(this);
        final TextView textGeneralRate = new TextView(this);
        textShade.setText("Enter the new shade rate:");
        textGeneralRate.setText("Enter the new general rate:");
        shadeRate.setNumStars(5);
        generalRate.setNumStars(5);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textGeneralRate);
        layout.addView(generalRate);
        layout.addView(textShade);
        layout.addView(shadeRate);
        builder.setView(layout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
                            String objectDB_Id = child.getKey();
                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
                            Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                            this.updateSite(s, objectDB_Id);
                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
                            assert s != null;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    private void updateSite(Site s, String objectDB_Id)
                    {
                        // Update data in Firebase
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("rate", (generalRate.getRating()*1.0) );
                        updates.put("shadeRate", (shadeRate.getRating()*1.0));
                        updates.put("mainRateReviewNum", (s.getMainRateReviewNum()+1));
                        updates.put("shadeRateReviewNum", (s.getShadeRateReviewNum()+1));
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
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

    public void locationEditButtonClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Select the new location:");
//        String[] options = {Location.North.name(), Location.Center.name(), Location.South.name()};
//        String[] options = {"Option 1", "Option 2", "Option 3"};
        LinearLayout layout = new LinearLayout(this);
        final String[] options = {Location.North.name(), Location.Center.name(), Location.South.name()};

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layout.addView(spinner);


//        layout.setOrientation(LinearLayout.VERTICAL);
//        final ListView listView = new ListView(this);
//        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));
//        layout.addView(listView);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
                            String objectDB_Id = child.getKey();
                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
                            Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                            this.updateSite(s, objectDB_Id);
                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
                            assert s != null;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    private void updateSite(Site s, String objectDB_Id)
                    {
                        // Update data in Firebase
                        Map<String, Object> updates = new HashMap<>();
//                        int position = listView.getCheckedItemPosition();
                        String position = spinner.getSelectedItem().toString();
                        updates.put("location",position);
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
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

    public void detailEditButtonClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        builder.setMessage("Enter the new detail:");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
                            String objectDB_Id = child.getKey();
                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
                            Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                            this.updateSite(s, objectDB_Id);
                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
                            assert s != null;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    private void updateSite(Site s, String objectDB_Id)
                    {
                        // Update data in Firebase
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("detail", input.getText().toString());
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
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

    public void addEventButtonClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
//        builder.setMessage("Enter the new shade rate:");
        final EditText inputDate = new EditText(this);
        final EditText inputDetail = new EditText(this);
        final TextView textdetail = new TextView(this);
        final TextView textDate = new TextView(this);
        textDate.setText("Enter the date of the event:");
        textdetail.setText("Enter meeting detail:");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textDate);
        layout.addView(inputDate);
        layout.addView(textdetail);
        layout.addView(inputDetail);
        builder.setView(layout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //get site DB - ID
                final DatabaseReference databaseReference = database.getReference();
                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
                            String objectDB_Id = child.getKey();
                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
                            Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                            this.updateSite(s, objectDB_Id);
                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
                            assert s != null;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    private void updateSite(Site s, String objectDB_Id)
                    {
                        // Update data in Firebase
                        Map<String, Object> updates = new HashMap<>();
                        GroupEvent event = new GroupEvent(inputDate.getText().toString(), 0, inputDetail.getText().toString());
                        updates.put("event", event);
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
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

    public void loadImageButtonClicked(View view){
        Intent intent = new Intent(AdminDetailActivity.this, ImageLoad.class);
        intent.putExtra("siteID", selectedShapeID);
        startActivity(intent);
    }



}