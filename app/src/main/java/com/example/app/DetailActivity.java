package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
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
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity
{
    // creating object of ViewPager
    ViewPager mViewPager;
    private Button eventGroup;
    private AlertDialog.Builder builder;

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
        setContentView(R.layout.activity_detail);
        getSelectedShape();
//        updateSelectedName();
        this.updateImageDetailList(selectedShapeName);
        eventGroup = findViewById(R.id.event);


//        setValues();

//        setContentView(R.layout.activity_detail);

        //-------------- first page --------------------
        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, images);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        loadEvent();
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
                        "\n\nRate: " + s.getRate() + "/5" +
                        "\n\nShade Rate: " + s.getShadeRate() + "/5" +
                        "\n\nDetail: " + s.getDetail();
                siteName.setText(s.getName());
                siteDetail.setText(detail);
                if (s.getEvent() != null)
                    eventGroup.setVisibility(View.VISIBLE);
                else
                    eventGroup.setVisibility(View.GONE);
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

//    private void setValues()
//    {
////        Site selectedShape = new Site("2","Herzliya beach", "picture/shvil.jpg", 3, "bla lba", Location.South);
//
//        TextView siteName = (TextView) findViewById(R.id.siteName);
//        TextView siteDetail = (TextView) findViewById(R.id.siteDetail);
////        ImageView iv = (ImageView) findViewById(R.id.mainImage);
//        String detail = "Name: " + selectedShape.getName() +
//                        "\n\nArea: " + selectedShape.getLocation() +
//                        "\n\nRate: " + selectedShape.getRate() + "/10" +
//                        "\n\nDetail: " + selectedShape.getDetail();
//        siteName.setText(selectedShape.getName());
//        siteDetail.setText(detail);
////        setImageList();
//
////        iv.setImageResource(selectedShape.getImage()); //Todo: do all image like this
//    }

//    private void setImageList() {
//        myStorage = FirebaseStorage.getInstance().getReference().child("picture/"+pictureName).listAll();
//    }

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
                                        Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
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

    public void showGroupEvent(View view){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        //get site DB - ID
//        final DatabaseReference databaseReference = database.getReference();
////        databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot)
////            {
////                // get all of the children at this level.
////                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
////                for (DataSnapshot child : children) { //not really for, have only one with this ID!
////                    String objectDB_Id = child.getKey();
////                    Site s = child.getValue(Site.class);
////                    this.updateSite(s, objectDB_Id);
////                    assert s != null;
////                }
////            }
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////            }
////            private void updateSite(Site s, String objectDB_Id)
////            {
////                // show data from Firebase
////                builder.setTitle("Group Event");
////                builder.setMessage("Date: " + s.getEvent().getDateEvent() +
////                                "\nParticipants: " + s.getEvent().getPeopleEvent() +
////                                  "\nDetail: " + s.getEvent().getMeetingDetail());
////                final EditText input = new EditText(DetailActivity.this);
////            }
////        });
//        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                //get site DB - ID
//                final DatabaseReference databaseReference = database.getReference();
//                databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot)
//                    {
//                        // get all of the children at this level.
//                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                        for (DataSnapshot child : children) { //not really for, have only one with this ID!
//                            String objectDB_Id = child.getKey();
//                            Log.d("adminDetailActivity", "child.getKey()="+objectDB_Id);
//                            Site s = child.getValue(Site.class);
////                                s.updateRate(rating);
//                            this.updateSite(s, objectDB_Id);
//                            Log.d("adminDetailActivity", "s.getName()="+s.getName());
//                            assert s != null;
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                    private void updateSite(Site s, String objectDB_Id)
//                    {
//                        // show data from Firebase
//                        builder.setTitle("Group Event");
//                        builder.setMessage("Date: " + s.getEvent().getDateEvent() +
//                                "\nParticipants: " + s.getEvent().getPeopleEvent() +
//                                "\nDetail: " + s.getEvent().getMeetingDetail());
//                        final EditText input = new EditText(DetailActivity.this);
//                        // Update data in Firebase
//                        Map<String, Object> updates = new HashMap<>();
//                        updates.put("event/peopleEvent", (s.getEvent().getPeopleEvent() + 1));
//                        String path = "site/" + objectDB_Id;
//                        database.getReference(path).updateChildren(updates);
//                    }
//                });
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
        //start dialog:
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadEvent(){
        builder = new AlertDialog.Builder(this);
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
                    Site s = child.getValue(Site.class);
                    this.updateSite(s, objectDB_Id);
                    assert s != null;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            private void updateSite(Site s, String objectDB_Id)
            {
                // show data from Firebase
                builder.setTitle("Group Event");
                builder.setMessage("Date: " + s.getEvent().getDateEvent() +
                                "\nParticipants: " + s.getEvent().getPeopleEvent() +
                                  "\nDetail: " + s.getEvent().getMeetingDetail());
                final EditText input = new EditText(DetailActivity.this);
            }
        });
        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
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
                        updates.put("event/peopleEvent", (s.getEvent().getPeopleEvent() + 1));
                        String path = "site/" + objectDB_Id;
                        database.getReference(path).updateChildren(updates);
                    }
                });
                this.welcomeMessage();
            }

            private void welcomeMessage() {
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
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

//    private void setimageView(String shapeName) {
//        shapeName = "Jerusalem Forest";
//        myStorage = FirebaseStorage.getInstance().getReference().child("picture/"+shapeName);//+shapeName
//        try {
//            final File localTempFile = File.createTempFile("shvil", "jpg");
//            class myOnSuccess implements OnSuccessListener {
//                public String name;
//                @Override
//                public void onSuccess(Object o) {
//                    {
//                        ListResult listResult = (ListResult) o;
//                        for(StorageReference file:listResult.getItems())
//                        {
//                            Log.d("firebaseFailed", file.getName());
//                            name = file.getName();
//
//                            file.getFile(localTempFile).addOnSuccessListener((new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                    Log.d("firebaseFailed", "in!");
//
//                                    Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
//                                    Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
//                                    images.add(bitmap);
//                                    Log.d("firebaseFailed", ""+images.size());
//                                }
//                            })).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e("firebaseFailed", e.getMessage());
//                                }
//                            });
//                        }
//                        mViewPagerAdapter.notifyDataSetChanged();
////                            Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
////                            Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
////                            ((ImageView) findViewById(R.id.mainImage)).setImageBitmap(bitmap);
//                    }
//                }
//            }
//            myOnSuccess myOS = new myOnSuccess();
//            myStorage.listAll()
//                    .addOnSuccessListener(myOS
////                            new OnSuccessListener<ListResult>() {
////                        @Override
////                        public void onSuccess(ListResult listResult) {
////
////                            for(StorageReference file:listResult.getItems())
////                            {
////                                Log.d("firebaseFailed", file.getName());
////
////                                file.getFile(localTempFile).addOnSuccessListener((new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
////                                    @Override
////                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
////                                        Log.d("firebaseFailed", "in!");
////
////                                        Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
////                                        Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
////                                        images.add(bitmap);
////                                        Log.d("firebaseFailed", ""+images.size());
////                                    }
////                                })).addOnFailureListener(new OnFailureListener() {
////                                    @Override
////                                    public void onFailure(@NonNull Exception e) {
////                                        Log.e("firebaseFailed", e.getMessage());
////                                    }
////                                });
////                            }
//////                            Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
//////                            Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
//////                            ((ImageView) findViewById(R.id.mainImage)).setImageBitmap(bitmap);
////                        }
////                    }
//        ).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("firebaseFailed", "error!");
//                            Toast.makeText(DetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            Log.d("firebaseFailed_out", "going sleep");
//
////            sleep(100000);
//            Log.d("firebaseFailed_out", myOS.name);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}