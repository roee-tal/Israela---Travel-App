package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        setUpList();

//        setAdapter(null);
        initSearchWidgets();
        initWidgets();
//        setupData();
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

        Site circle = new Site("11", "Jerusalem Forest", "Jerusalem Forest/image1.jfif", 9, "bla lba", Location.Center, 3);
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

        Site triangle = new Site("1","Tel Aviv beach", "Tel Aviv beach/telAviv1.jfif", 0, "bla lba", Location.Center, 3);
        shapeList.add(triangle);

        Site square = new Site("2","Herzliya beach", "picture/shvil.jpg", 3, "bla lba", Location.South, 3);
        shapeList.add(square);

        Site rectangle = new Site("3","Rectangle", "picture/shvil.jpg", 1, "bla lba", Location.South, 3);
        shapeList.add(rectangle);

        Site octagon = new Site("4","Octagon", "picture/shvil.jpg", 7.6, "bla lba", Location.North, 3);
        shapeList.add(octagon);

        Site circle2 = new Site("5", "Circle 2", "picture/shvil.jpg", 1.2, "bla lba", Location.North, 3);
        shapeList.add(circle2);

        Site triangle2 = new Site("6","Triangle 2", "picture/shvil.jpg", 9, "bla lba", Location.Center, 3);
        shapeList.add(triangle2);

        Site square2 = new Site("7","Square 2", "picture/shvil.jpg", 9, "bla lba", Location.Center, 3);
        shapeList.add(square2);

        Site rectangle2 = new Site("8","Rectangle 2", "picture/shvil.jpg", 9, "bla lba", Location.Center, 3);
        shapeList.add(rectangle2);

        Site octagon2 = new Site("9","Octagon 2", "picture/shvil.jpg", 9, "bla lba", Location.Center, 3);
        shapeList.add(octagon2);
//        //--------------------------------------------
//        // push all the objects to firebase:
//        for (Site site: shapeList) {
//            mDatabase.child("site").push().setValue(site);
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
////            shapeList.add(site);
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
                Site selectShape = (Site) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailActivity.class);
                showDetail.putExtra("id",selectShape.getId());
                showDetail.putExtra("name",selectShape.getName());
                startActivity(showDetail);
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