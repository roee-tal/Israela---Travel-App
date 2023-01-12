package com.example.app.model;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.app.activities.MainActivity;
import com.example.app.model.objects.Category;
import com.example.app.model.objects.Location;
import com.example.app.model.objects.Site;
import com.example.app.model.objects.SortType;
import com.example.app.modelView.MainMV;
import com.example.app.modelView.adapters.ShapeAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class MainModel
{
    private FirebaseDatabase myRealTimeDB;

    public static ArrayList<Site> shapeList;
    private ShapeAdapter adapter;
    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private MainMV mainMV;
    private MainActivity mainActivity;


    public MainModel(ArrayList<Site> shapeList, MainMV mainMV, MainActivity mainActivity, ShapeAdapter adapter) {
        this.shapeList = shapeList;
        this.mainMV = mainMV;
        this.mainActivity = mainActivity;
        this.myRealTimeDB = FirebaseDatabase.getInstance();
    }

    public void allFilterTappedForFlow(SortType type) {
        selectedFilters.clear();
        selectedFilters.add("all");

        mainActivity.unSelectAllFilterButtons();
        mainActivity.lookSelectedAll();

//        shapeList.clear();
        Query query = myRealTimeDB.getReference().child("site");
        // Execute the query and retrieve the matching items
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                shapeList.clear();
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

                Log.d("Filter-mainModel", "shapeList.size=" + shapeList.size());
                adapter.notifyDataSetChanged();
                Log.d("Filter-mainModel", "type=" + type);
                Log.d("Filter-mainModel", "shapeList=" + shapeList);
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

    public void setupData()
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

        Site triangle = new Site("1","Tel Aviv beach", 0.2, "The Tel Aviv beach stretches for ages along the whole western edge of the city, moving from historic Yaffo to the northern port near Park Hayarkon. \nStretching over 14 kilometers along the Mediterranean, this sparkling coastline has a little bit of everything, and activities for each season. \nWhether you’re looking to soak up the summer sun, use the stormy winter weather to catch a few waves, or jog along the tayelet, we break down all the beaches in Tel Aviv and their distinctive characters below so you can choose your perfect beach.\n", Location.Center, 3,1,1, null, Category.swimming);
        shapeList.add(triangle);

        Site square = new Site("2","Herzliya beach", 3, "At Herzliya, tourists will find a white sand blanket, a beautiful promenade, a marina, a rescue station, as well as excellent restaurants and bars with fun parties. \nThere are no breakwaters on Herzliya beach, which makes it a dream for any surfer. \nIn the beach sports club, you can take diving and surfing lessons, rent surfboards, catamarans, baidarkas, and kayaks.\n", Location.South, 3,1,1, null, Category.swimming);
        shapeList.add(square);

        Site rectangle = new Site("3","Mezada", 1, "Masada is not only important because it is a UNESCO World Heritage Site or an ancient fortress occupying a breathtaking, strategic location high on a flat plateau above the Dead Sea, but because of its symbolic importance of determination and heroism which continues to this day with many Israeli soldiers sworn in here.\n" +
                "\n\n" +
                "This mountain is one of the greatest archaeological sites in Israel and, perhaps, across the world. \nIts dramatic ascent can now be made by cable-car, but the drama and imagery that this site portrays is no less powerful than it ever was. \nMany people opt to join a Masada tour, enjoying a guide who will bring the site to life.\n", Location.South, 3,1,1, null, Category.track);
        shapeList.add(rectangle);

        Site octagon = new Site("4","Hermon", 2.6, "Us Israelis love to brag and boast about the Hermon Mountain, probably because it’s one of the only places that lets us pretend we’re wintering in Europe rather than the Mediterranean. \nBut there’s more to the Hermon than winter time snow, and it’s not just the mountain in northern Israel that’s worth a visit. \nThe area is known for its multitude of trails for amateurs and seasoned hikers alike, as well as babbling brooks and streams. \nIf you’re looking for something a little more specific, check out these great ideas for a good day in Israel’s northernmost peaks.\n", Location.North, 3,1,1, null, Category.track);
        shapeList.add(octagon);

        Site circle2 = new Site("5", "Nahal Hazbany", 1.2, "Nahal Snir (also known as the Hasbani River) has a national park that is found in the Northern Galilee. \nThe park consists of a picnic area, two man made pools and an entrance to the Snir River itself. \nOne of the man made pools is for wading and has a waterfall flowing into it. \nThe other pool, is a refuge for rare water plants such as the Yellow Pond Lily.\n" +
                "\n\n" +
                "In addition to the pools in this reserve, is the Snir Stream Ravine, a short hiking trail of 1km that one can do either inside the water or outside it. \nTo the left of this trail are small waterfalls bringing water from the Dan River into the Snir. \nThis nature reserve is great for the whole family. \nDon’t forget to explore Park Chushim, a sensory park within the reserve which is great for young kids. \nThe Nahal Snir National Park needs advanced booking. \nTo book click here\n", Location.North, 3,1,1, null, Category.swimming);
        shapeList.add(circle2);

        Site triangle2 = new Site("6","Dead Sea", 4, "The Dead Sea, known in Hebrew as Yam Ha-Melakh (the Sea of Salt) is the lowest point on earth. \nIt’s surrounded by the stunning landscape of the Negev Desert. \nThe shores of the Dead Sea are the lowest point on the surface of the earth. \nThe saline waters of the lake means no fish can survive in the salty waters, hence the name. \nThe other result of the salty water is their renowned health and healing properties of the mud. \nYou can also float naturally in them. \nThere are tours to the Dead Sea available from across Israel which allow you to experience this yourself. \nAlternatively, staying at a Dead Sea Hotel provides additional spa and treatment experiences.\n", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(triangle2);

        Site square2 = new Site("7","Ben Shemen Forest", 4, "Want to find the best trails in Ben Shemen Forest for an adventurous hike or a family trip? AllTrails has 14 great trails for hiking and more. " +
                "\nEnjoy hand-curated trail maps, along with reviews and photos from nature lovers like you. \nReady for your next hike or bike ride? Explore one of 4 easy hiking trails in Ben Shemen Forest that are great for the whole family. " +
                "\nLooking for a more strenuous hike? We've got you covered, with trails ranging from 40 to 843 meters in elevation gain. " +
                "\nWhatever you have planned for the day, you can find the perfect trail for your next trip to Ben Shemen Forest.\n", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(square2);

        Site rectangle2 = new Site("8","Ein Hanatziv", 3, "Check out this 2.3-km out-and-back trail near Sde Eliyahu, Northern District HaZafon. \nGenerally considered an easy route, it takes an average of 28 min to complete. \nThis trail is great for hiking and walking, and it's unlikely you'll encounter many other people while exploring. \nThe trail is open year-round and is beautiful to visit anytime. \nDogs are welcome, but must be on a leash.\n", Location.Center, 3,1,1, null, Category.picnic);
        shapeList.add(rectangle2);

        Site octagon2 = new Site("9","Red Canyon", 1.8, "The Red Canyon in the Eilat Mountains is one of Israel’s most beautiful yet accessible hiking trails. \nEntrance to the park is completely free. \nTwenty minutes north of Eilat, this stunning geological wonder offers an undisturbed hike through the natural canyons. \nThe Red Canyon gets its name from the phenomenon which occurs when sunlight hits the reddish rock that lines the canyon. \nIt then glows with an intense reddish color. \nThe rock is varied in color and in patches is shades of white and yellow. \nThe colors come from the sandstone that’s been carved by wind and water through the ages.\n", Location.Center, 3,1,1, null, Category.picnic);
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

//        //-------------------------------------------

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
//        --------------------------------------------
//        //******************************************************************
//        // push all the objects to firebase:
//        for (Site site: shapeList) {
//            mDatabase.child("site").push().setValue(site);
////            mDatabase.child("site").child(site.getId()).push().setValue("reviews");
//        }
//        //-
    }

    public void filterList(String status, SortType type)
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

    public boolean loadByText(String str) {
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

    public void setAdapter(ShapeAdapter adapter) {
        this.adapter = adapter;
    }

    public void allFilterTapped() {
        selectedFilters.clear();
        selectedFilters.add("all");
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

                Log.d("allFilterTapped-model", "shapeList.size=" + shapeList.size());
                adapter.notifyDataSetChanged();
                Log.d("allFilterTapped-model", "shapeList.size=" + shapeList.size());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });

    }

    public ArrayList<String> getselectedFilters() {
        return selectedFilters;
    }

}