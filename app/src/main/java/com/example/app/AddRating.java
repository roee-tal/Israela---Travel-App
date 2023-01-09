package com.example.app;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddRating extends AppCompatActivity {
    String selectedShapeName;
    String selectedShapeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_add);
        getSelectedShape();
        final String shapeName = selectedShapeName;
        final String shapeID = selectedShapeID;
        // initiate rating bar and a button
        final RatingBar mainRateBar = (RatingBar) findViewById(R.id.mainRateBar);
        Button submitButton = (Button) findViewById(R.id.submitButton);
        // perform click event on button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get values and then displayed in a toast
                int totalStars =  mainRateBar.getNumStars();
                final double rating = mainRateBar.getRating();
                Log.d("AddRating", "totalStars=" + totalStars + " rating=" + rating);


                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference databaseReference = database.getReference();
                    databaseReference.child("site").orderByChild("id").equalTo(shapeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        /**
                         * This method will be invoked any time the data on the database changes.
                         * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
                         * @param dataSnapshot
                         */
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // get all of the children at this level.
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) { //not really for, have only one with this ID!
                                String objectDB_Id = child.getKey();
                                Log.d("AddRating", "child.getKey()="+objectDB_Id);
                                Site s = child.getValue(Site.class);
//                                s.updateRate(rating);
                                this.updateSite(s, objectDB_Id);
                                Log.d("AddRating", "s.getName()="+s.getName());
                                assert s != null;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                        private void updateSite(Site s, String objectDB_Id)
                        {
                            s.updateRate(rating);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("rate", s.getRate());
                            updates.put("mainRateReviewNum", (s.getMainRateReviewNum()+1));
                            String path = "site/" + objectDB_Id;
                            database.getReference(path).updateChildren(updates);
                            Log.d("AddRating", "in update - s.rate="+s.getRate());
                        }
                    });
                Intent showDetail = new Intent(getApplicationContext(), DetailActivity.class);
                showDetail.putExtra("id",shapeID);
                showDetail.putExtra("name",shapeName);
                startActivity(showDetail);
            }
        });
    }

    private void getSelectedShape()
    {
        Intent previousIntent = getIntent();
        selectedShapeID = previousIntent.getStringExtra("id");
        selectedShapeName = previousIntent.getStringExtra("name");
        Log.d("AddRating", "name="+selectedShapeName);
        Log.d("AddRating", "id="+selectedShapeID);
    }

}
