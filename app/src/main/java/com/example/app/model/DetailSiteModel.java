package com.example.app.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.app.model.objects.Review;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.User;
import com.example.app.modelView.DetailSiteMV;
import com.example.app.modelView.adapters.ViewPagerAdapter;
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

public class DetailSiteModel {
    DetailSiteMV detailSiteMV;
    final ArrayList<Site>  siteSingalAtList = new ArrayList<Site>();
    DatabaseReference siteRef;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private StorageReference myStorage;


    public DetailSiteModel(DetailSiteMV detailSiteMV){
        this.detailSiteMV = detailSiteMV;
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

                detailSiteMV.setVal(s);
            }
        });
    }

    // Upload the images of the site using FirebaseStorage
    public void updateImageDetailList(String shapeID,ArrayList<Bitmap> images, ViewPagerAdapter mViewPagerAdapter) {
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
                            detailSiteMV.showToast("Error During Picture Retrieved");
//                            Toast.makeText(DetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * When click on submit button
     * @param mainRateBar the rating bar(with stars)
     * @param dialog
     * @param selectedShapeID the chosen site
     */
    public void bSubmit(RatingBar mainRateBar, AlertDialog dialog, String selectedShapeID) {
        final int totalStars = mainRateBar.getNumStars();
        final double rating = mainRateBar.getRating();
        Log.d("AddRating", "totalStars=" + totalStars + " rating=" + rating);

        databaseReference.child("site").orderByChild("id").equalTo(selectedShapeID).addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) { //not really for, have only one with this ID!
                    String objectDB_Id = child.getKey();
                    Log.d("AddRating", "child.getKey()=" + objectDB_Id);
                    Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                    //Update the site rating
                    updateSite(s, objectDB_Id, rating, database);
                    Log.d("AddRating", "s.getName()=" + s.getName());
                    assert s != null;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        dialog.dismiss();

    }


    private void updateSite(Site s, String objectDB_Id, double rating, FirebaseDatabase database)  {

        s.updateRate(rating); //According to 'updateRate' calculation
        //Update the databasse
        Map<String, Object> updates = new HashMap<>();
        updates.put("rate", s.getRate());
        updates.put("mainRateReviewNum", (s.getMainRateReviewNum()+1));
        String path = "site/" + objectDB_Id;
        database.getReference(path).updateChildren(updates);
        Log.d("AddRating", "in update - s.rate="+s.getRate());
    }

    /**
     * Get the event and update his details
     * @param shapeID
     */
    public void LoadEvent(String shapeID) {
        final DatabaseReference databaseReference = database.getReference();
        databaseReference.child("site").orderByChild("id").equalTo(shapeID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) { //not really for, have only one with this ID!
                    String objectDB_Id = child.getKey();
                    Site s = child.getValue(Site.class);
                    //If has event
                    if (s.getEvent() != null)
                        this.updateSite(s, objectDB_Id);
                    assert s != null;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            private void updateSite(Site s, String objectDB_Id)
            {
                detailSiteMV.updateTripDetails(s);
            }
        });
    }

    /**
     * update when join to group
     * @param selectedID the site id
     */
    public void whenJoinGroup(String selectedID) {
        final DatabaseReference databaseReference = database.getReference();
        databaseReference.child("site").orderByChild("id").equalTo(selectedID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) { //not really for, have only one with this ID!
                    String objectDB_Id = child.getKey();
                    Log.d("DetailActivity", "child.getKey()=" + objectDB_Id);
                    //To object
                    Site s = child.getValue(Site.class);
                    this.addToUser(s, objectDB_Id);//Update the database
                    assert s != null;
                }
            }

            private void addToUser(Site s, String objectDB_id) {
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                String id = user.getUid();
                FirebaseFirestore fstore = FirebaseFirestore.getInstance();
                fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            User usr = doc.toObject(User.class);
                            Log.d("TAG", "user-check = " + usr.getEmail());
                            if (!usr.addEvents(s, objectDB_id)) { //already joined to this event
                                detailSiteMV.showToast("You already joined to this event!");
//                                Toast.makeText(DetailActivity.this, "You already joined to this event!", Toast.LENGTH_LONG).show();
                                Log.d("DetailActivity", "already joined to this event!");
                                return;
                            }
                            this.updateUser(usr);
                            updateSite(s, objectDB_id); // only if already joined to this event
                        }
                    }

                    private void updateUser(User usr) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("events", usr.getEvents());

                        fstore.collection("Users").document(id).update("events", usr.getEvents());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            private void updateSite(Site s, String objectDB_Id) {
                // Update data in Firebase, add 1
                Map<String, Object> updates = new HashMap<>();
                updates.put("event/peopleEvent", (s.getEvent().getPeopleEvent() + 1));
                String path = "site/" + objectDB_Id;
                database.getReference(path).updateChildren(updates);
                detailSiteMV.welcomeMessage(); // welcome
            }


        });
    }

    public void addReview(String name, String review, AlertDialog dialog) {
        if(!name.isEmpty() && !review.isEmpty()){
            Review newReview = new Review(name, review);
            siteRef.child("reviews").push().setValue(newReview);
            detailSiteMV.showToast("success");

            dialog.dismiss();
        }else{
            detailSiteMV.showToast("try again");

//            Toast.makeText(DetailActivity.this, "try again", Toast.LENGTH_SHORT).show();
        }
    }

    // Show the reviews
    public void showReview(ArrayAdapter<String> adapterReview, ArrayList<String> arrayListReviews) {
        siteRef.child("reviews").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayListReviews.clear();
                Log.d("reviews", "start");
                Iterable<DataSnapshot> children = snapshot.getChildren();
                for (DataSnapshot child : children) {
                    // To review object
                    Review r = child.getValue(Review.class);
                    assert r != null;
                    String review = "\nName: " + r.getName()+ "\n";
                    review += "Review: " + r.getReview()+ "\n";

                    arrayListReviews.add(review);
                    Log.d("reviews", review);
                }
                adapterReview.notifyDataSetChanged(); //notify
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("getParsedShape", "siteSingalAtList.size="+siteSingalAtList.size());
            }
        });


    }
}
