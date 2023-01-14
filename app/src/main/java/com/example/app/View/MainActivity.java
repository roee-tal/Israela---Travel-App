
package com.example.app.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.app.R;
import com.example.app.model.helpClassesModel.ImageLoad;
import com.example.app.model.objects.Category;
import com.example.app.model.objects.Location;
import com.example.app.modelView.MainMV;
import com.example.app.modelView.adapters.ShapeAdapter;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.SortType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private FirebaseDatabase myRealTimeDB;

    public static ArrayList<Site> shapeList = new ArrayList<Site>();
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
    boolean centerSelected = false;
    boolean southSelected = false;
    boolean northSelected = false;

    boolean swimmingSelected = false;
    boolean trackSelected = false;
    boolean picnicSelected = false;

    private Button southButton, centetButton, northButton, allButton, picnicButton, swimmingButton, trackButton;
    private Button down2upRateButton, up2downRateButton, nameAscButton, nameDescButton;

    private SearchView searchView;
    private BottomNavigationView nav;
    private BottomNavigationView nav_admin;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    private int white, darkGray, red;
    private MainMV mainMV;

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


        mainMV = new MainMV(shapeList, this,adapter);

        check_bottom_user();
        setUpList();
        mainMV.setAdapter(adapter);

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
        mainMV.allFilterTappedForFlow(SortType.RateUp2Down);
    }

    private void check_bottom_user(){
        nav_admin = findViewById(R.id.bottom_nav_a);
        nav = findViewById(R.id.bottom_nav);

        mainMV.checkIfUser();


            }

    private void initViewFlipper(BottomNavigationView nav, BottomNavigationView nav_admin, ViewFlipper navViewFlipper) {
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
    }

    public void applyAdmin() {
                ViewFlipper navViewFlipper = findViewById(R.id.nav_view_flipper);
                initViewFlipper(nav,nav_admin,navViewFlipper);
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
            public void applyUser() {
                ViewFlipper navViewFlipper = findViewById(R.id.nav_view_flipper);
                initViewFlipper(nav,nav_admin,navViewFlipper);
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

                            case R.id.events:
                                mainMV.popUpEvents(MainActivity.this);
                                break;

                            default:

                        }

                        return true;
                    }

                });
            }


    public void getTheEvent(ArrayAdapter<String> adapterEvent) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.events_list_view, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        lv.setAdapter(adapterEvent);

        alertDialog.show();
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

                mainMV.bAdd(mName,mRate,mShadeRate,mDetails,mLocation,mCategory);
                    dialog.dismiss();
//
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

    public void unSelectAllFilterButtons()
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
                return mainMV.loadByText(str);
            }
        });
    }

    /**
     * Note: The first part of this function use only for debug and develop
     */
    private void setupData()
    {
        // this will hold our collection of all Site's.
        mainMV.setupData();
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
                mainMV.is_admin(selectShape);
            }
        });

    }

    public void allFilterTapped(View view)
    {
        unSelectAllFilterButtons();
        lookSelected(allButton);
        mainMV.allFilterTapped();


    }

    public void centerFilterTapped(View view)
    {
        mainMV.filterList("Center", null);
        Log.d("centerFilterTapped", "sort with mainModel");
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
        mainMV.filterList("South", null);
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
        mainMV.filterList("North", null);
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
        mainMV.filterList("track", null);
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
        mainMV.filterList("picnic", null);
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
        mainMV.filterList("swimming", null);
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
        mainMV.checkForFilter(SortType.RateDown2Up);
        unSelectAllSortButtons();
        lookSelected(down2upRateButton);
    }

    public void up2downRateApped(View view)
    {
        mainMV.checkForFilter(SortType.RateUp2Down);
        unSelectAllSortButtons();
        lookSelected(up2downRateButton);
    }

    public void nameASCTapped(View view)
    {
        mainMV.checkForFilter(SortType.NameDown2Up);
        unSelectAllSortButtons();
        lookSelected(nameAscButton);
    }

    public void nameDESCTapped(View view)
    {

        mainMV.checkForFilter(SortType.NameUp2Down);
        unSelectAllSortButtons();
        lookSelected(nameDescButton);
    }

    private void setAdapter(ArrayList<Site> shapeList)
    {
        adapter = new ShapeAdapter(getApplicationContext(), 0, this.shapeList);
        listView.setAdapter(adapter);
    }

    public void lookSelectedAll() {
        lookSelected(allButton);
    }


}