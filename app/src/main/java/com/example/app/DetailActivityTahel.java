package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.File;
import java.util.ArrayList;

public class DetailActivityTahel extends AppCompatActivity {

    // creating object of ViewPager
    ViewPager mViewPager;

    // images array
    final ArrayList<Bitmap>  images = new ArrayList<Bitmap>();
    final ArrayList<Site>  siteSingalAtList = new ArrayList<Site>();

    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    Site selectedShape;
    String selectedShapeName;
    Button addReviewButton;
    private StorageReference myStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.try1);
        getSelectedShape();


//        updateSelectedName();
        this.updateImageDetailList(selectedShapeName);

//        setValues();

//        setContentView(R.layout.activity_detail);

        //-------------- first page --------------------
        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(DetailActivityTahel.this, images);


        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        addReviewButton = (Button) findViewById(R.id.addReviewButton);
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReview();
            }
        });

    }

    private void getSelectedShape()
    {
        Intent previousIntent = getIntent();
        String parsedStringID = previousIntent.getStringExtra("id");
        selectedShapeName = previousIntent.getStringExtra("name");
        Log.d("updateSelectedName", "name="+selectedShapeName);
        Log.d("updateSelectedName", "id="+parsedStringID);
        getParsedShape(parsedStringID);
    }

    private void getParsedShape(String parsedID)
    {
//        final ArrayList<Site> siteList = new ArrayList<Site>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
//        Site selectedShape = new Site("2","Herzliya beach", "picture/shvil.jpg", 3, "bla lba", Location.South);

                TextView siteName = (TextView) findViewById(R.id.siteName);
                TextView siteDetail = (TextView) findViewById(R.id.siteDetail);
//        ImageView iv = (ImageView) findViewById(R.id.mainImage);
                String detail = "Name: " + s.getName() +
                        "\n\nArea: " + s.getLocation() +
                        "\n\nRate: " + s.getRate() + "/10" +
                        "\n\nDetail: " + s.getDetail();
                siteName.setText(s.getName());
                siteDetail.setText(detail);
//        setImageList();

//        iv.setImageResource(selectedShape.getImage()); //Todo: do all image like this
            }});
        //--------------------------------------------
    }


    private void updateImageDetailList(String shapeName) {
//        shapeName = "Jerusalem Forest";
        Log.d("updateImageDetailList", "shapeName="+shapeName);
        myStorage = FirebaseStorage.getInstance().getReference().child("picture/"+shapeName);//+shapeName
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
                                                              Toast.makeText(DetailActivityTahel.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailActivityTahel.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addReview() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailActivityTahel.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_add_review, null);
        final EditText mName = (EditText) mView.findViewById(R.id.name);
        final EditText mReview = (EditText) mView.findViewById(R.id.review);
        Button mAdd = (Button) mView.findViewById(R.id.add);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mName.getText().toString().isEmpty() && !mReview.getText().toString().isEmpty()){

//                    mDatabase.child("site").push().setValue(site);
                    Toast.makeText(DetailActivityTahel.this, "success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }else{
                    Toast.makeText(DetailActivityTahel.this, "try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
