package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
{
    private FirebaseDatabase myRealTimeDB;

    public static ArrayList<Site> shapeList = new ArrayList<Site>();
    //    private List<SortType> sortTypeList = new ArrayList<SortType>();
    private ShapeAdapter adapter;

    private ListView listView;
    private Button sortButton;
    private Button filterButton;
    private LinearLayout filterView1;
    private LinearLayout filterView2;
    private LinearLayout sortView;

    boolean sortHidden = true;
    boolean filterHidden = true;
    Location[] allLocations = {Location.Center, Location.North, Location.South}; //Location.All,
    boolean centerSelected = false;
    boolean southSelected = false;
    boolean northSelected = false;

    private Button southButton, centetButton, northButton, allButton;
    private Button down2upRateButton, up2downRateButton, nameAscButton, nameDescButton;

    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private SearchView searchView;
    private BottomNavigationView nav;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private int white, darkGray, red;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRealTimeDB = FirebaseDatabase.getInstance();
//        try {
//            final File localTempFile = File.createTempFile("shvil", "jpg");
//            myRealTimeDB.child("picture/shvil.jpg").getFile(localTempFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
////                            Log.d("firebaseFailed", "in!");
////                            Log.d("firebaseFailed", localTempFile.getName());
//
//                            Toast.makeText(MainActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
//                            Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
//                            ((ImageView) findViewById(R.id.mainImage)).setImageBitmap(bitmap);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.contactt:
                        startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        break;

                    case R.id.sign_out:
                        areYouSureMessage();

                    default:

                }

                return true;
            }
        });

        setUpList();

//        setAdapter(null);
        initSearchWidgets();
        initWidgets();
        setupData();
        setUpOnclickListener();
        hideFilter();
        hideSort();
        initColors();
        unSelectAllSortButtons();
        lookSelected(up2downRateButton);
        lookSelected(allButton);
        unSelectAllFilterButtons();
        allFilterTappedForFlow(SortType.RateUp2Down);
    }


    private void areYouSureMessage(){
        new AlertDialog.Builder(this).setMessage("Are you sure you want to exit?").
                setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                })
                .setNegativeButton("No",null).show();
    }


    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {

                    finish();
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                }
                else {
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                }
            }

        });



    }

    private void allFilterTappedForFlow(SortType type) {
        selectedFilters.clear();
        selectedFilters.add("all");

        unSelectAllFilterButtons();
        lookSelected(allButton);

        shapeList.clear();
        Query query = myRealTimeDB.getReference().child("site");
        // Execute the query and retrieve the matching items
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = snapshot.getChildren();
                Log.d("allFilterTapped", "empty:" + shapeList.size());
                for (DataSnapshot child : children) {
                    Site s = child.getValue(Site.class);
                    assert s != null;
                    if (!shapeList.contains(s))
                        shapeList.add(s);
                    Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());
                    Log.d("v", "site name=" + s.getName());
                }
                if (type != null)
                    sortList(type);

                Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());
                adapter.notifyDataSetChanged();
                Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());
            }

            private void sortList(SortType type) {
                switch (type) {
                    case RateUp2Down:
                        Collections.sort(shapeList, Site.rateSort);
                        Collections.reverse(shapeList);
                        break;
                    case RateDown2Up:
                        Collections.sort(shapeList, Site.rateSort);
                        break;
                    case NameUp2Down:
                        Collections.sort(shapeList, Site.nameAscending);
                        Collections.reverse(shapeList);
                        break;
                    case NameDown2Up:
                        Collections.sort(shapeList, Site.nameAscending);
                        break;
                    default:
                        Log.d("sortList", "default !" + type);
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initColors()
    {
        white = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        red = ContextCompat.getColor(getApplicationContext(), R.color.red);
        darkGray = ContextCompat.getColor(getApplicationContext(), R.color.darkerGray);
    }

    private void unSelectAllSortButtons()
    {
        lookUnSelected(down2upRateButton);
        lookUnSelected(up2downRateButton);
        lookUnSelected(nameAscButton);
        lookUnSelected(nameDescButton);
    }

    private void unSelectAllFilterButtons()
    {
        lookUnSelected(allButton);
        lookUnSelected(northButton);
        lookUnSelected(centetButton);
        lookUnSelected(southButton);
    }

    private void lookSelected(Button parsedButton)
    {
        parsedButton.setTextColor(white);
        parsedButton.setBackgroundColor(red);
    }

    private void lookUnSelected(Button parsedButton)
    {
        parsedButton.setTextColor(red);
        parsedButton.setBackgroundColor(darkGray);
    }

    private void initWidgets()
    {
        sortButton = (Button) findViewById(R.id.sortButton);
        filterButton = (Button) findViewById(R.id.filterButton);
        filterView1 = (LinearLayout) findViewById(R.id.filterTabsLayout);
        filterView2 = (LinearLayout) findViewById(R.id.filterTabsLayout2);
        sortView = (LinearLayout) findViewById(R.id.sortTabsLayout2);

        southButton = (Button) findViewById(R.id.southFilter);
        centetButton = (Button) findViewById(R.id.centerFilter);
        northButton = (Button) findViewById(R.id.northFilter);
        allButton  = (Button) findViewById(R.id.allFilter);

        down2upRateButton = (Button) findViewById(R.id.down2upRate);
        up2downRateButton = (Button) findViewById(R.id.up2downRate);
        nameAscButton  = (Button) findViewById(R.id.nameAsc);
        nameDescButton  = (Button) findViewById(R.id.nameDesc);
    }

    private void initSearchWidgets()
    {
        searchView = (SearchView) findViewById(R.id.shapeListSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str)
            {
                shapeList.clear();
                currentSearchText = str;
                Log.d("initSearchWidgets", "str="+str);

                Query query = myRealTimeDB.getReference().child("site").orderByChild("name").startAt(str).endAt(str + "\uf8ff");
                // Execute the query and retrieve the matching items
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        shapeList.clear();
                        Log.d("initSearchWidgets", "empty:"+shapeList.size());
                        for (DataSnapshot child : children) {
                            Site s = child.getValue(Site.class);
                            assert s != null;
                            shapeList.add(s);
                            Log.d("initSearchWidgets", "shapeList.size="+shapeList.size());
                            Log.d("initSearchWidgets", "site name="+s.getName());
                        }

                        Log.d("initSearchWidgets", "shapeList.size="+shapeList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "shapeList.size="+shapeList.size());

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle error
                    }
                });


//                ArrayList<Site> filteredShapes = new ArrayList<Site>();
//
//                for(Site site : shapeList)
//                {
//                    if(site.getName().toLowerCase().contains(str.toLowerCase()))
//                    {
//                        if(selectedFilters.contains("all"))
//                        {
//                            filteredShapes.add(site);
//                        }
//                        else
//                        {
//                            for(String filter: selectedFilters)
//                            {
//                                if (site.getName().toLowerCase().contains(filter))
//                                {
//                                    filteredShapes.add(site);
//                                }
//                            }
//                        }
//                    }
//                }
//                setAdapter(filteredShapes);

                return false;
            }
        });
    }

    private void setupData()
    {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Site circle = new Site("11", "Jerusalem Forest", "Jerusalem Forest/image1.jfif", 5, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(circle);

//        final String[] ans = new String[1];
//        mDatabase.child("site").child("1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    ans[0] = String.valueOf(task.getResult().getValue());
//                    Toast.makeText(MainActivity.this, ans[0], Toast.LENGTH_LONG).show();
//
//                    Log.d("firebase", "ans[0]");
//                    Log.d("firebase", ans[0]);
//                }
//            }
//        });

        Site triangle = new Site("1","Tel Aviv beach", "Tel Aviv beach/telAviv1.jfif", 0.2, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(triangle);

        Site square = new Site("2","Herzliya beach", "Tel Aviv beach/telAviv1.jfif", 3, "bla lba", Location.South, 3,1,1, null);
        shapeList.add(square);

        Site rectangle = new Site("3","Rectangle", "Tel Aviv beach/telAviv1.jfif", 1, "bla lba", Location.South, 3,1,1, null);
        shapeList.add(rectangle);

        Site octagon = new Site("4","Octagon", "Tel Aviv beach/telAviv1.jfif", 2.6, "bla lba", Location.North, 3,1,1, null);
        shapeList.add(octagon);

        Site circle2 = new Site("5", "Circle 2", "Tel Aviv beach/telAviv1.jfif", 1.2, "bla lba", Location.North, 3,1,1, null);
        shapeList.add(circle2);

        Site triangle2 = new Site("6","Triangle 2", "Tel Aviv beach/telAviv1.jfif", 4, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(triangle2);

        Site square2 = new Site("7","Square 2", "Tel Aviv beach/telAviv1.jfif", 4, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(square2);

        Site rectangle2 = new Site("8","Rectangle 2", "Tel Aviv beach/telAviv1.jfif", 3, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(rectangle2);

        Site octagon2 = new Site("9","Octagon 2", "Tel Aviv beach/telAviv1.jfif", 1.8, "bla lba", Location.Center, 3,1,1, null);
        shapeList.add(octagon2);
//        //--------------------------------------------
//        ******************************************************************8
//        // push all the objects to firebase:
//        for (Site site: shapeList) {
//            mDatabase.child("site").push().setValue(site);
////            mDatabase.child("site").child(site.getId()).push().setValue("reviews");
//        }
//        //--------------------------------------------
        // this will hold our collection of all Site's.
        final ArrayList<Site> siteList = new ArrayList<Site>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("site").addValueEventListener(new ValueEventListener() {
            /**
             * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    Site s = child.getValue(Site.class);
                    siteList.add(s);
                    assert s != null;
                }
                //        shapeList.clear(); //only for self check of data loading
                for (Site site:siteList) {
//            shapeList.add(site);
                    Log.d("firebase0129", site.getRate() +"---"+ site.getName());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
        //--------------------------------------------
////        shapeList.clear(); //only for self check of data loading
//        for (Site site:siteList) {
//            shapeList.add(site);
//            Log.d("firebase0129", site.getRate() +"---"+ site.getName());
//        }

    }

    private void setUpList()
    {
        listView = (ListView) findViewById(R.id.shapesListView);

        setAdapter(shapeList);
    }

    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                final Site selectShape = (Site) (listView.getItemAtPosition(position));
                Intent showDetail;
                if (is_admin(selectShape)) {
                    showDetail = new Intent(getApplicationContext(), AdminDetailActivity.class);
                    Log.d("MainActivity", "----ADMIN Done :)");
                }
                else {
                    showDetail = new Intent(getApplicationContext(), DetailActivity.class); //Todo: change detail load by user not work with my user
                    Log.d("MainActivity", "----admin NOT done");
                }

                showDetail.putExtra("id",selectShape.getId());
                showDetail.putExtra("name",selectShape.getName());
                startActivity(showDetail);
            }

            private boolean is_admin(Site selectShape) {
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
                                showDetail[0] = new Intent(getApplicationContext(), AdminDetailActivity.class);
                                Log.d("MainActivity", "ADMIN LOADED :)");
                            }
                            else {
                                Log.d("MainActivity", "is NOT admin");
                                showDetail[0] = new Intent(getApplicationContext(), DetailActivity.class);
                                Log.d("MainActivity", "admin NOT loaded");
                            }
                            showDetail[0].putExtra("id",selectShape.getId());
                            showDetail[0].putExtra("name",selectShape.getName());
                            startActivity(showDetail[0]);
                        }
                    }
                });
//                Log.d("MainActivity", "is admin = " + is_admin[0]);
                return true; //is_admin[0];
            }
        });

    }

    private void filterList(String status, SortType type)
    {
        String location;
        if(status != null && selectedFilters.contains(status)) { // in case of second click on location
            selectedFilters.remove(status); //in this case remove location from the filter list
        }
        else if (status != null) {
            selectedFilters.add(status);
            if (!status.equals("all") && selectedFilters.contains("all"))
                selectedFilters.remove("all");
        }

        shapeList.clear();
        Log.d("filterList", "status="+status);
        for (String filter : selectedFilters) {
            Query query = myRealTimeDB.getReference().child("site").orderByChild("location").equalTo(filter);
            // Execute the query and retrieve the matching items
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot)
                {
                    // get all of the children at this level.
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    Log.d("initSearchWidgets", "empty:" + shapeList.size());
                    for (DataSnapshot child : children) {
                        Site s = child.getValue(Site.class);
                        assert s != null;
                        if (!shapeList.contains(s))
                            shapeList.add(s);
                        Log.d("initSearchWidgets", "shapeList.size=" + shapeList.size());
                        Log.d("initSearchWidgets", "site name=" + s.getName());
                    }
                    if (type != null)
                        sortList(type);
                    Log.d("initSearchWidgets", "shapeList.size=" + shapeList.size());
                    adapter.notifyDataSetChanged();
                    Log.d("initSearchWidgets", "shapeList.size=" + shapeList.size());

                }

                private void sortList(SortType type) {
                    switch (type) {
                        case RateUp2Down:
                            Collections.sort(shapeList, Site.rateSort);
                            Collections.reverse(shapeList);
                            break;
                        case RateDown2Up:
                            Collections.sort(shapeList, Site.rateSort);
                            break;
                        case NameUp2Down:
                            Collections.sort(shapeList, Site.nameAscending);
                            Collections.reverse(shapeList);
                            break;
                        case NameDown2Up:
                            Collections.sort(shapeList, Site.nameAscending);
                            break;
                        default:
                            Log.d("sortList", "default !" + type);
                            break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle error
                }
            });
        }
//        ArrayList<Site> filteredShapes = new ArrayList<Site>();
//        for(Site site : shapeList)
//        {
//            for(String filter: selectedFilters)
//            {
//                // filter for location name:
//                location = isLocation(filter);
//                if(!location.equals("NOTHING") && site.getLocation().name().equals(location))
//                {
//                    if(currentSearchText == "")
//                    {
//                        filteredShapes.add(site); //Todo: add here wheris.. query and delete 'for(Site site : shapeList)'
//                    }
//                    else
//                    {
//                        if(site.getName().toLowerCase().contains(currentSearchText.toLowerCase()))
//                        {
//                            filteredShapes.add(site); //Todo: add here wheris.. query
//                        }
//                    }
//                }
//
////                // filter for other free text:            //Todo: verify that not need this 'else if':
////                else if(site.getName().toLowerCase().contains(filter))
////                {
////                    if(currentSearchText == "")
////                    {
////                        filteredShapes.add(site); //Todo: add here wheris.. query and delete 'for(Site site : shapeList)'
////                    }
////                    else
////                    {
////                        if(site.getName().toLowerCase().contains(currentSearchText.toLowerCase()))
////                        {
////                            filteredShapes.add(site); //Todo: add here wheris.. query
////                        }
////                    }
////                }
//            }
//        }
//
//        setAdapter(filteredShapes);
    }

    private String isLocation(String filter) {
        for (Location l:allLocations) {
            if (filter.equals(l.name())) {
                Log.println(Log.DEBUG, "check1236", "Find Location - " + l.name().toLowerCase());
                return l.name();
            }
        }
        return "NOTHING";
    }

    public void allFilterTapped(View view)
    {
        selectedFilters.clear();
        selectedFilters.add("all");

        unSelectAllFilterButtons();
        lookSelected(allButton);

        shapeList.clear();
        Query query = myRealTimeDB.getReference().child("site");
        // Execute the query and retrieve the matching items
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = snapshot.getChildren();
                Log.d("allFilterTapped", "empty:" + shapeList.size());
                for (DataSnapshot child : children) {
                    Site s = child.getValue(Site.class);
                    assert s != null;
                    if (!shapeList.contains(s))
                        shapeList.add(s);
                    Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());
                    Log.d("v", "site name=" + s.getName());
                }

                Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());
                adapter.notifyDataSetChanged();
                Log.d("allFilterTapped", "shapeList.size=" + shapeList.size());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });


//        setAdapter(shapeList);
    }

    public void centerFilterTapped(View view)
    {
        filterList("Center", null);
        if (!centerSelected) {
            lookSelected(centetButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(centetButton);
        }
        centerSelected = !centerSelected;
    }

    public void southFilterTapped(View view)
    {
        filterList("South", null);
        if (!southSelected) {
            lookSelected(southButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(southButton);
        }
        southSelected = !southSelected;
    }

    public void northFilterTapped(View view)
    {
        filterList("North", null);
        if (!northSelected) {
            lookSelected(northButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(northButton);
        }
        northSelected = !northSelected;
    }

    public void showFilterTapped(View view)
    {
        if(filterHidden == true)
        {
            filterHidden = false;
            showFilter();
        }
        else
        {
            filterHidden = true;
            hideFilter();
        }
    }

    public void showSortTapped(View view)
    {
        if(sortHidden == true)
        {
            sortHidden = false;
            showSort();
        }
        else
        {
            sortHidden = true;
            hideSort();
        }
    }

    private void hideFilter()
    {
        searchView.setVisibility(View.GONE);
        filterView1.setVisibility(View.GONE);
        filterView2.setVisibility(View.GONE);
        filterButton.setText("FILTER");
    }

    private void showFilter()
    {
        searchView.setVisibility(View.VISIBLE);
        filterView1.setVisibility(View.VISIBLE);
        filterView2.setVisibility(View.VISIBLE);
        filterButton.setText("HIDE");
    }

    private void hideSort()
    {
        sortView.setVisibility(View.GONE);
        sortButton.setText("SORT");
    }

    private void showSort()
    {
        sortView.setVisibility(View.VISIBLE);
        sortButton.setText("HIDE");
    }

    public void down2upRateApped(View view)
    {
//        Collections.sort(shapeList, Site.rateSort);
        checkForFilter(SortType.RateDown2Up);
//        shapeList.add(new sortType(SortType.RateDown2Up));
//        adapter.notifyDataSetChanged();
        unSelectAllSortButtons();
        lookSelected(down2upRateButton);
    }

    public void up2downRateApped(View view)
    {
//        Collections.sort(shapeList, Site.rateSort);
//        Collections.reverse(shapeList);
        checkForFilter(SortType.RateUp2Down);
        unSelectAllSortButtons();
        lookSelected(up2downRateButton);
    }

    public void nameASCTapped(View view)
    {
//        Collections.sort(shapeList, Site.nameAscending);
        checkForFilter(SortType.NameDown2Up);
        unSelectAllSortButtons();
        lookSelected(nameAscButton);
    }

    public void nameDESCTapped(View view)
    {
//        Collections.sort(shapeList, Site.nameAscending);
//        Collections.reverse(shapeList);
        checkForFilter(SortType.NameUp2Down);
        unSelectAllSortButtons();
        lookSelected(nameDescButton);
    }

    private void checkForFilter(SortType type)
    {
        if(selectedFilters.contains("all"))
        {
            allFilterTappedForFlow(type);
        }
        else
        {
            filterList(null, type);
        }
    }

    private void setAdapter(ArrayList<Site> shapeList)
    {
        adapter = new ShapeAdapter(getApplicationContext(), 0, this.shapeList);
        listView.setAdapter(adapter);
    }
}