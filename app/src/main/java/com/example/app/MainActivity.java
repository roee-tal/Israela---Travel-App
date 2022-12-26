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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.google.firebase.firestore.DocumentReference;
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
    private LinearLayout filterView3;
    private LinearLayout sortView;

    boolean sortHidden = true;
    boolean filterHidden = true;
    Location[] allLocations = {Location.Center, Location.North, Location.South}; //Location.All,
    boolean centerSelected = false;
    boolean southSelected = false;
    boolean northSelected = false;

    boolean swimmingSelected = false;
    boolean trackSelected = false;
    boolean picnicSelected = false;

    private Button southButton, centetButton, northButton, allButton, picnicButton, swimmingButton, trackButton;
    private Button down2upRateButton, up2downRateButton, nameAscButton, nameDescButton;

    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private SearchView searchView;
    private BottomNavigationView nav;
    private BottomNavigationView nav_admin;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private int white, darkGray, red;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRealTimeDB = FirebaseDatabase.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        check_bottom_user();
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

    private void check_bottom_user(){
        nav_admin = findViewById(R.id.bottom_nav_a);
        nav = findViewById(R.id.bottom_nav);
        ViewFlipper navViewFlipper = findViewById(R.id.nav_view_flipper);
        ViewGroup parent = (ViewGroup) nav.getParent();
        if (parent != null) {
            parent.removeView(nav);
        }
        navViewFlipper.addView(nav);

        ViewGroup parent2 = (ViewGroup) nav_admin.getParent();
        if (parent2 != null) {
            parent2.removeView(nav_admin);
        }// Replace "navigationBar1" with the view for your first navigation bar
        navViewFlipper.addView(nav_admin);

        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        DocumentReference df =  fstore.collection("Users").document(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        if (document.getString("isUser").equals("1")) {
                            applyUser();

                        }
                        else{
                            Log.d("TAG", "dasssssssssssssss: " + document.getData());

                            applyAdmin();

                        }
                    }
                }
            }

            private void applyAdmin() {
                navViewFlipper.setDisplayedChild(1);
                nav_admin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.sign_out_admin:
                                areYouSureMessage();
                                break;

                            case R.id.u:
                                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                                break;

                            case R.id.p:
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                break;

                            case R.id.addS:
                                addSiteTapped();
                        }

                        return true;
                    }
                });
            }

            private void applyUser() {
                navViewFlipper.setDisplayedChild(0);
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
                                break;

                            default:

                        }

                        return true;
                    }
                });
            }
        });
    }


    public void addSiteTapped()  {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_add_site, null);

        final String[] locations = {Location.North.name(), Location.Center.name(), Location.South.name()};


        final EditText mName = (EditText) mView.findViewById(R.id.name);
        final EditText mRate = (EditText) mView.findViewById(R.id.rate);
        final EditText mShadeRate = (EditText) mView.findViewById(R.id.shade_rate);
        final EditText mDetails = (EditText) mView.findViewById(R.id.details);
        final EditText mLocation = (EditText) mView.findViewById(R.id.location);
        final EditText mCategory = (EditText) mView.findViewById(R.id.category);

        Button mAdd = (Button) mView.findViewById(R.id.add);
        Button loadImage = (Button) mView.findViewById(R.id.loadImage);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageLoad.class);
                intent.putExtra("siteID", "13");
                startActivity(intent);
            }
        });
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = mName.getText().toString();
                String rate = mRate.getText().toString();
                String shade_rate = mShadeRate.getText().toString();
                String details = mDetails.getText().toString();
                String location = mLocation.getText().toString();
                String category = mCategory.getText().toString();
                Location currLocation = Location.Center;
                Category currCategory = Category.track;

                if (location.equals("center")) currLocation = Location.Center;
                else if (location.equals("north")) currLocation = Location.North;
                else if  (location.equals("south")) currLocation = Location.South;
                else location = "";

                if (category.equals("track")) currCategory = Category.track;
                else if (category.equals("picnic")) currCategory = Category.picnic;
                else if  (category.equals("swimming")) currCategory = Category.swimming;
                else category = "";

                if ( name.isEmpty() || rate.isEmpty() ||
                        shade_rate.isEmpty() || details.isEmpty() || location.isEmpty() || category.isEmpty()) {

                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();

                }else{


                    Site newSite = new Site("13",name, Integer.parseInt(rate), details, currLocation, Integer.parseInt(shade_rate),1,1, null, currCategory);

                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("site").push().setValue(newSite);

                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
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
        lookUnSelected(trackButton);
        lookUnSelected(swimmingButton);
        lookUnSelected(picnicButton);
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
        filterView3 = (LinearLayout) findViewById(R.id.filterTabsLayout3);
        sortView = (LinearLayout) findViewById(R.id.sortTabsLayout2);

        southButton = (Button) findViewById(R.id.southFilter);
        centetButton = (Button) findViewById(R.id.centerFilter);
        northButton = (Button) findViewById(R.id.northFilter);
        allButton  = (Button) findViewById(R.id.allFilter);

        picnicButton = (Button) findViewById(R.id.picnicFilter);
        swimmingButton = (Button) findViewById(R.id.swimmingFilter);
        trackButton  = (Button) findViewById(R.id.trackFilter);

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

        Site triangle = new Site("1","Tel Aviv beach", 0.2, "The Tel Aviv beach stretches for ages along the whole western edge of the city, moving from historic Yaffo to the northern port near Park Hayarkon. Stretching over 14 kilometers along the Mediterranean, this sparkling coastline has a little bit of everything, and activities for each season. Whether you’re looking to soak up the summer sun, use the stormy winter weather to catch a few waves, or jog along the tayelet, we break down all the beaches in Tel Aviv and their distinctive characters below so you can choose your perfect beach.", Location.Center, 3,1,1, null, Category.swimming);
        shapeList.add(triangle);

        Site square = new Site("2","Herzliya beach", 3, "At Herzliya, tourists will find a white sand blanket, a beautiful promenade, a marina, a rescue station, as well as excellent restaurants and bars with fun parties. There are no breakwaters on Herzliya beach, which makes it a dream for any surfer. In the beach sports club, you can take diving and surfing lessons, rent surfboards, catamarans, baidarkas, and kayaks.", Location.South, 3,1,1, null, Category.swimming);
        shapeList.add(square);

        Site rectangle = new Site("3","Mezada", 1, "Masada is not only important because it is a UNESCO World Heritage Site or an ancient fortress occupying a breathtaking, strategic location high on a flat plateau above the Dead Sea, but because of its symbolic importance of determination and heroism which continues to this day with many Israeli soldiers sworn in here.\n" +
                "\n\n" +
                "This mountain is one of the greatest archaeological sites in Israel and, perhaps, across the world. Its dramatic ascent can now be made by cable-car, but the drama and imagery that this site portrays is no less powerful than it ever was. Many people opt to join a Masada tour, enjoying a guide who will bring the site to life.", Location.South, 3,1,1, null, Category.track);
        shapeList.add(rectangle);

        Site octagon = new Site("4","Hermon", 2.6, "Us Israelis love to brag and boast about the Hermon Mountain, probably because it’s one of the only places that lets us pretend we’re wintering in Europe rather than the Mediterranean. But there’s more to the Hermon than winter time snow, and it’s not just the mountain in northern Israel that’s worth a visit. The area is known for its multitude of trails for amateurs and seasoned hikers alike, as well as babbling brooks and streams. If you’re looking for something a little more specific, check out these great ideas for a good day in Israel’s northernmost peaks.", Location.North, 3,1,1, null, Category.track);
        shapeList.add(octagon);

        Site circle2 = new Site("5", "Nahal Hazbany", 1.2, "Nahal Snir (also known as the Hasbani River) has a national park that is found in the Northern Galilee. \nThe park consists of a picnic area, two man made pools and an entrance to the Snir River itself. \nOne of the man made pools is for wading and has a waterfall flowing into it. \nThe other pool, is a refuge for rare water plants such as the Yellow Pond Lily.\n" +
                "\n\n" +
                "In addition to the pools in this reserve, is the Snir Stream Ravine, a short hiking trail of 1km that one can do either inside the water or outside it. \nTo the left of this trail are small waterfalls bringing water from the Dan River into the Snir. \nThis nature reserve is great for the whole family. \nDon’t forget to explore Park Chushim, a sensory park within the reserve which is great for young kids. \nThe Nahal Snir National Park needs advanced booking. \nTo book click here", Location.North, 3,1,1, null, Category.swimming);
        shapeList.add(circle2);

        Site triangle2 = new Site("6","Dead Sea", 4, "The Dead Sea, known in Hebrew as Yam Ha-Melakh (the Sea of Salt) is the lowest point on earth. \nIt’s surrounded by the stunning landscape of the Negev Desert. \nThe shores of the Dead Sea are the lowest point on the surface of the earth. \nThe saline waters of the lake means no fish can survive in the salty waters, hence the name. \nThe other result of the salty water is their renowned health and healing properties of the mud. \nYou can also float naturally in them. \nThere are tours to the Dead Sea available from across Israel which allow you to experience this yourself. \nAlternatively, staying at a Dead Sea Hotel provides additional spa and treatment experiences.", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(triangle2);

        Site square2 = new Site("7","Ben Shemen Forest", 4, "Want to find the best trails in Ben Shemen Forest for an adventurous hike or a family trip? AllTrails has 14 great trails for hiking and more. " +
                "\nEnjoy hand-curated trail maps, along with reviews and photos from nature lovers like you. \nReady for your next hike or bike ride? Explore one of 4 easy hiking trails in Ben Shemen Forest that are great for the whole family. " +
                "\nLooking for a more strenuous hike? We've got you covered, with trails ranging from 40 to 843 meters in elevation gain. " +
                "\nWhatever you have planned for the day, you can find the perfect trail for your next trip to Ben Shemen Forest.", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(square2);

        Site rectangle2 = new Site("8","Ein Hanatziv", 3, "Check out this 2.3-km out-and-back trail near Sde Eliyahu, Northern District HaZafon. \nGenerally considered an easy route, it takes an average of 28 min to complete. \nThis trail is great for hiking and walking, and it's unlikely you'll encounter many other people while exploring. \nThe trail is open year-round and is beautiful to visit anytime. \nDogs are welcome, but must be on a leash.", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(rectangle2);

        Site octagon2 = new Site("9","Red Canyon", 1.8, "The Red Canyon in the Eilat Mountains is one of Israel’s most beautiful yet accessible hiking trails. \nEntrance to the park is completely free. \nTwenty minutes north of Eilat, this stunning geological wonder offers an undisturbed hike through the natural canyons. \nThe Red Canyon gets its name from the phenomenon which occurs when sunlight hits the reddish rock that lines the canyon. \nIt then glows with an intense reddish color. \nThe rock is varied in color and in patches is shades of white and yellow. \nThe colors come from the sandstone that’s been carved by wind and water through the ages.", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(octagon2);

        Site circle = new Site("10", "Jerusalem Forest", 5, "In the early years of the state, Jewish National Fund planted thousands of trees along the western edge of Jerusalem, creating a green belt.[1]\n" +
                "\n" +
                "The first tree of the Jerusalem Forest was planted in 1956 by the second President of Israel, Itzhak Ben-Zvi. At its peak, the area of the forest covered 4,700 dunams. Over the years, the boundaries of the forest have receded due to urban expansion, and it now covers only 1,250 dunams.[2]\n" +
                "\n" +
                "The Yad Vashem Holocaust museum is located in the forest below Mount Herzl. In the middle of the forest, between Yad Vashem and Ein Kerem, is Mercaz Tzippori, a youth hostel. On this same campus is the office of \"The Adam Institute for Democracy and Peace\", an Israeli non-profit organization that runs educational programs promoting tolerance and coexistence.\n" +
                "\n" +
                "The forest acts as a refuge for wildlife, and there are packs of jackals that inhabit the forest."
                , Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(circle);

//        //--------------------------------------------
//        //******************************************************************
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
                is_admin(selectShape);
//                if (is_admin(selectShape)) {
//                    showDetail = new Intent(getApplicationContext(), AdminDetailActivity.class);
//                    Log.d("MainActivity", "----ADMIN Done :)");
//                }
//                else {
//                    showDetail = new Intent(getApplicationContext(), DetailActivity.class); //Todo: change detail load by user not work with my user
//                    Log.d("MainActivity", "----admin NOT done");
//                }
//
//                showDetail.putExtra("id",selectShape.getId());
//                showDetail.putExtra("name",selectShape.getName());
//                startActivity(showDetail);
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
//        ArrayList<Site> tempShapeList = new ArrayList<Site>();
        Log.d("filterList", "status="+status);
        if (selectedFilters.isEmpty())
            allFilterTappedForFlow(SortType.RateUp2Down); //choose 'all' like the start
        for (String filter : selectedFilters) {
            if (is_category(filter)) {
                Query query = myRealTimeDB.getReference().child("site").orderByChild("category").equalTo(filter);
                // Execute the query and retrieve the matching items
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // get all of the children at this level.
                        Iterable<DataSnapshot> children = snapshot.getChildren();
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
            else{
                Query query = myRealTimeDB.getReference().child("site").orderByChild("location").equalTo(filter);
                // Execute the query and retrieve the matching items
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
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

    private boolean is_category(String filter) {
        for (Category c : Category.values()) {
            if (c.name().equals(filter))
                return true;
        }
        return false;
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

    public void trackFilterTapped(View view)
    {
        filterList("track", null);
        if (!trackSelected) {
            lookSelected(trackButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(trackButton);
        }
        trackSelected = !trackSelected;
    }

    public void picnicFilterTapped(View view)
    {
        filterList("picnic", null);
        if (!picnicSelected) {
            lookSelected(picnicButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(picnicButton);
        }
        picnicSelected = !picnicSelected;
    }

    public void swimmingFilterTapped(View view)
    {
        filterList("swimming", null);
        if (!swimmingSelected) {
            lookSelected(swimmingButton);
            lookUnSelected(allButton);
        }
        else {
            lookUnSelected(swimmingButton);
        }
        swimmingSelected = !swimmingSelected;
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
        filterView3.setVisibility(View.GONE);
        filterButton.setText("FILTER");
    }

    private void showFilter()
    {
        searchView.setVisibility(View.VISIBLE);
        filterView1.setVisibility(View.VISIBLE);
        filterView2.setVisibility(View.VISIBLE);
        filterView3.setVisibility(View.VISIBLE);
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