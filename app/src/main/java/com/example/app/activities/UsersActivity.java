
package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.app.helpClasses.BottomNavActivity;
import com.example.app.R;
import com.example.app.modelView.adapters.UserAdapter;
import com.example.app.modelView.UsersMV;
import com.example.app.model.objects.SortType;
import com.example.app.model.objects.User;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity
{

    public static ArrayList<User> UsersList = new ArrayList<User>();
    private ListView listView;
    private Button nameAscButton, messages, allButton;
    private SearchView searchView;

    UsersMV usersMV;
    private UserAdapter adapter;

    private int white, darkGray, red;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        //Initialize MV of this activity
        usersMV = new UsersMV(this, adapter);

        initSearchWidgets();
        initWidgets();
        // For the menu bar in the bottom
        clickOnBottomNav();
        setUpList();
        setUpOnclickListener();
        initColors();
        lookSelected(nameAscButton);
        lookSelected(allButton);
        allFiltering();
    }


    private void clickOnBottomNav(){
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);
    }

    private void allFiltering(){

        unSelectAllFilterButtons();
        lookSelected(allButton);
        usersMV.bAllFilter(adapter,UsersList);

    }

    private void NameFilter(SortType type) {

        unSelectAllFilterButtons();
        lookSelected(allButton);

        usersMV.bNameFilter(adapter, UsersList, type);

            }


    private void initColors()
    {
        white = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        red = ContextCompat.getColor(getApplicationContext(), R.color.red);
        darkGray = ContextCompat.getColor(getApplicationContext(), R.color.darkerGray);
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

    private void unSelectAllFilterButtons()
    {
        lookUnSelected(allButton);
        lookUnSelected(messages);
        lookUnSelected(nameAscButton);
    }

    private void initWidgets()
    {

        nameAscButton  = (Button) findViewById(R.id.nameAsc);
        messages = (Button) findViewById(R.id.Messag);
        allButton = (Button) findViewById(R.id.all);
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

                usersMV.bSearch(adapter,UsersList,str);

                return false;
            }
        });
    }

    private void setUpList()
    {
        listView = (ListView) findViewById(R.id.shapesListView);

        setAdapter(UsersList);
    }

    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                User selectUser = (User) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailUserActivity.class);
                showDetail.putExtra("email",selectUser.getEmail());
                startActivity(showDetail);
            }
        });
    }

    public void AllTapped(View view)
    {
        allFiltering();
    }

    public void nameASCTapped(View view)
    {
        NameFilter(SortType.UserNAmeDown2Up);
        unSelectAllFilterButtons();
        lookSelected(nameAscButton);
    }

    public void messageTapped(View view) {

        unSelectAllFilterButtons();
        lookSelected(messages);
        usersMV.bMessage(view, adapter, UsersList);
    }


    private void setAdapter(ArrayList<User> UsersList)
    {
        adapter = new UserAdapter(getApplicationContext(), 0, UsersList);
        listView.setAdapter(adapter);
    }

    public void notifyAdapter(UserAdapter adapter) {
        adapter.notifyDataSetChanged();
    }
}