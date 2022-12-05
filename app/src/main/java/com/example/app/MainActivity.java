package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
{
    private StorageReference myStorage;

    public static ArrayList<Site> shapeList = new ArrayList<Site>();

    private ListView listView;
    private Button sortButton;
    private Button filterButton;
    private LinearLayout filterView1;
    private LinearLayout filterView2;
    private LinearLayout sortView;

    boolean sortHidden = true;
    boolean filterHidden = true;

    private Button circleButton, squareButton, rectangleButton, triangleButton, octagonButton, allButton;
    private Button idAscButton, idDescButton, nameAscButton, nameDescButton;

    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private SearchView searchView;

    private int white, darkGray, red;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myStorage = FirebaseStorage.getInstance().getReference().child("picture/shvil.jpg");
        try {
            final File localTempFile = File.createTempFile("shvil", "jpg");
            myStorage.getFile(localTempFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
                            ((ImageView) findViewById(R.id.mainImage)).setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (IOException e){
            e.printStackTrace();
        }

        initSearchWidgets();
        initWidgets();
        setupData();
        setUpList();
        setUpOnclickListener();
        hideFilter();
        hideSort();
        initColors();
        lookSelected(idAscButton);
        lookSelected(allButton);
        selectedFilters.add("all");
    }

    private void initColors()
    {
        white = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        red = ContextCompat.getColor(getApplicationContext(), R.color.red);
        darkGray = ContextCompat.getColor(getApplicationContext(), R.color.darkerGray);
    }


    private void unSelectAllSortButtons()
    {
        lookUnSelected(idAscButton);
        lookUnSelected(idDescButton);
        lookUnSelected(nameAscButton);
        lookUnSelected(nameDescButton);
    }

    private void unSelectAllFilterButtons()
    {
        lookUnSelected(allButton);
        lookUnSelected(circleButton);
        lookUnSelected(rectangleButton);
        lookUnSelected(octagonButton);
        lookUnSelected(triangleButton);
        lookUnSelected(squareButton);
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

        circleButton = (Button) findViewById(R.id.circleFilter);
        squareButton = (Button) findViewById(R.id.squareFilter);
        rectangleButton = (Button) findViewById(R.id.rectangleFilter);
        triangleButton  = (Button) findViewById(R.id.triangleFilter);
        octagonButton  = (Button) findViewById(R.id.octagonFilter);
        allButton  = (Button) findViewById(R.id.allFilter);

        idAscButton  = (Button) findViewById(R.id.idAsc);
        idDescButton  = (Button) findViewById(R.id.idDesc);
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
            public boolean onQueryTextChange(String s)
            {
                currentSearchText = s;
                ArrayList<Site> filteredShapes = new ArrayList<Site>();

                for(Site site : shapeList)
                {
                    if(site.getName().toLowerCase().contains(s.toLowerCase()))
                    {
                        if(selectedFilters.contains("all"))
                        {
                            filteredShapes.add(site);
                        }
                        else
                        {
                            for(String filter: selectedFilters)
                            {
                                if (site.getName().toLowerCase().contains(filter))
                                {
                                    filteredShapes.add(site);
                                }
                            }
                        }
                    }
                }
                setAdapter(filteredShapes);

                return false;
            }
        });
    }

    private void setupData()
    {
        Site circle = new Site("0", "Circle", "picture/shvil.jpg", 9, "bla lba", Location.Center);
        shapeList.add(circle);

        Site triangle = new Site("1","Triangle", "picture/shvil.jpg", 0, "bla lba", Location.Center);
        shapeList.add(triangle);

        Site square = new Site("2","Square", "picture/shvil.jpg", 3, "bla lba", Location.South);
        shapeList.add(square);

        Site rectangle = new Site("3","Rectangle", "picture/shvil.jpg", 1, "bla lba", Location.South);
        shapeList.add(rectangle);

        Site octagon = new Site("4","Octagon", "picture/shvil.jpg", 7.6, "bla lba", Location.North);
        shapeList.add(octagon);

        Site circle2 = new Site("5", "Circle 2", "picture/shvil.jpg", 1.2, "bla lba", Location.North);
        shapeList.add(circle2);

        Site triangle2 = new Site("6","Triangle 2", "picture/shvil.jpg", 9, "bla lba", Location.Center);
        shapeList.add(triangle2);

        Site square2 = new Site("7","Square 2", "picture/shvil.jpg", 9, "bla lba", Location.Center);
        shapeList.add(square2);

        Site rectangle2 = new Site("8","Rectangle 2", "picture/shvil.jpg", 9, "bla lba", Location.Center);
        shapeList.add(rectangle2);

        Site octagon2 = new Site("9","Octagon 2", "picture/shvil.jpg", 9, "bla lba", Location.Center);
        shapeList.add(octagon2);
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
                startActivity(showDetail);
            }
        });

    }



    private void filterList(String status)
    {
        if(status != null && !selectedFilters.contains(status))
            selectedFilters.add(status);

        ArrayList<Site> filteredShapes = new ArrayList<Site>();

        for(Site site : shapeList)
        {
            for(String filter: selectedFilters)
            {
                if(site.getName().toLowerCase().contains(filter))
                {
                    if(currentSearchText == "")
                    {
                        filteredShapes.add(site);
                    }
                    else
                    {
                        if(site.getName().toLowerCase().contains(currentSearchText.toLowerCase()))
                        {
                            filteredShapes.add(site);
                        }
                    }
                }
            }
        }

        setAdapter(filteredShapes);
    }




    public void allFilterTapped(View view)
    {
        selectedFilters.clear();
        selectedFilters.add("all");

        unSelectAllFilterButtons();
        lookSelected(allButton);

        setAdapter(shapeList);
    }

    public void triangleFilterTapped(View view)
    {
        filterList("triangle");
        lookSelected(triangleButton);
        lookUnSelected(allButton);
    }

    public void squareFilterTapped(View view)
    {
        filterList("square");
        lookSelected(squareButton);
        lookUnSelected(allButton);
    }

    public void octagonFilterTapped(View view)
    {
        filterList("octagon");
        lookSelected(octagonButton);
        lookUnSelected(allButton);
    }

    public void rectangleFilterTapped(View view)
    {
        filterList("rectangle");
        lookSelected(rectangleButton);
        lookUnSelected(allButton);
    }

    public void circleFilterTapped(View view)
    {
        filterList("circle");
        lookSelected(circleButton);
        lookUnSelected(allButton);
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

    public void idASCTapped(View view)
    {
        Collections.sort(shapeList, Site.idAscending);
        checkForFilter();
        unSelectAllSortButtons();
        lookSelected(idAscButton);
    }

    public void idDESCTapped(View view)
    {
        Collections.sort(shapeList, Site.idAscending);
        Collections.reverse(shapeList);
        checkForFilter();
        unSelectAllSortButtons();
        lookSelected(idDescButton);
    }

    public void nameASCTapped(View view)
    {
        Collections.sort(shapeList, Site.nameAscending);
        checkForFilter();
        unSelectAllSortButtons();
        lookSelected(nameAscButton);
    }

    public void nameDESCTapped(View view)
    {
        Collections.sort(shapeList, Site.nameAscending);
        Collections.reverse(shapeList);
        checkForFilter();
        unSelectAllSortButtons();
        lookSelected(nameDescButton);
    }

    private void checkForFilter()
    {
        if(selectedFilters.contains("all"))
        {
            if(currentSearchText.equals(""))
            {
                setAdapter(shapeList);
            }
            else
            {
                ArrayList<Site> filteredShapes = new ArrayList<Site>();
                for(Site site : shapeList)
                {
                    if(site.getName().toLowerCase().contains(currentSearchText))
                    {
                        filteredShapes.add(site);
                    }
                }
                setAdapter(filteredShapes);
            }
        }
        else
        {
            filterList(null);
        }
    }

    private void setAdapter(ArrayList<Site> shapeList)
    {
        ShapeAdapter adapter = new ShapeAdapter(getApplicationContext(), 0, shapeList);
        listView.setAdapter(adapter);
    }
}