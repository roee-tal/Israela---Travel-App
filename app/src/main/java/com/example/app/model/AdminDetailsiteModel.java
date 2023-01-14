package com.example.app.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.app.model.objects.GroupEvent;
import com.example.app.model.objects.Site;
import com.example.app.modelView.AdminDetailsiteMV;
import com.example.app.modelView.adapters.ViewPagerAdapter;
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

public class AdminDetailsiteModel {

    AdminDetailsiteMV adminDetailsiteMV;
    final ArrayList<Site> siteSingalAtList = new ArrayList<Site>();
    DatabaseReference siteRef;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private StorageReference myStorage;


    public AdminDetailsiteModel(AdminDetailsiteMV adminDetailsiteMV){
        this.adminDetailsiteMV = adminDetailsiteMV;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    /**
     * Gets the data of site and represents him
     * @param parsedID the id of the site
     */
    public void getParsedShape(String parsedID) {

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
                    siteRef = child.getRef();
                    //To object
                    Site s = child.getValue(Site.class);
                    siteSingalAtList.add(s);
                    this.setValues(s); // With help functions
                    Log.d("getParsedShape", "siteSingalAtList.size="+siteSingalAtList.size());
                    assert s != null;
                }
                Log.d("getParsedShape", "siteSingalAtList.size="+siteSingalAtList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            private void setValues(Site s) {

                adminDetailsiteMV.setVal(s);
            }
        });
    }

    // Upload the images of the site using FirebaseStorage
    public void updateImageDetailList(String shapeID, ArrayList<Bitmap> images, ViewPagerAdapter mViewPagerAdapter) {
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
//                                        Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
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
                            adminDetailsiteMV.showToast("Error During Picture Retrieved");
//                            Toast.makeText(DetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Update the new name in the database
     * @param selectedShapeID the site id
     * @param input the text to update
     */
    public void bUpdateName(String selectedShapeID, EditText input) {
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
                    //to object
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


    /**
     * Update the new rate in the database
     * @param selectedShapeID the site id
     * @param generalRate the rating bar with the stars
     */
    public void bUpdateRate(String selectedShapeID, RatingBar generalRate) {
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
                updates.put("mainRateReviewNum", (s.getMainRateReviewNum()+1));
                String path = "site/" + objectDB_Id;
                database.getReference(path).updateChildren(updates);
            }
        });
    }

    /**
     * Update the new location in the database
     * @param selectedShapeID the site id
     * @param spinner the spinner with the options
     */
    public void bUpdateLocation(String selectedShapeID, Spinner spinner) {
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
                updates.put("location",position); //the new location
                String path = "site/" + objectDB_Id;
                database.getReference(path).updateChildren(updates);
            }
        });
    }

    /**
     * Update the new details in the database
     * @param selectedShapeID the site id
     * @param input the new details
     */
    public void bUpdateDetails(String selectedShapeID, EditText input) {
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


    /**
     * add new trip in the database
     * @param selectedShapeID the site id
     * @param inputDate the date of trip
     * @param inputDetail the trip details
     */
    public void bAddEvent(String selectedShapeID, EditText inputDate, EditText inputDetail) {
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
}
