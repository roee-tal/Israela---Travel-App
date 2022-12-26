
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class UsersActivity extends AppCompatActivity
{

    public static ArrayList<User> UsersList = new ArrayList<User>();
    private ListView listView;
    private Button nameAscButton, messages, allButton;
    private ArrayList<String> selectedFilters = new ArrayList<String>();
    private String currentSearchText = "";
    private SearchView searchView;
    private BottomNavigationView nav;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private UserAdapter adapter;

    private int white, darkGray, red;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        initSearchWidgets();
        initWidgets();
        clickOnBottomNav();
        setUpList();
        setUpOnclickListener();
        initColors();
        lookSelected(nameAscButton);
        lookSelected(allButton);
        allFiltering();
    }


    private void clickOnBottomNav(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        nav = findViewById(R.id.bottom_nav_admin);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sign_out_admin:
                        areYouSureMessage();
                        break;

                    case R.id.u:
                        startActivity(new Intent(UsersActivity.this, UsersActivity.class));
                        break;

                    case R.id.p:
                        startActivity(new Intent(UsersActivity.this, MainActivity.class));
                        break;

                }
                return true;
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
                    gsc.signOut();
                    finish();
                    startActivity(new Intent(UsersActivity.this, StartActivity.class));
                }
                else {
                    startActivity(new Intent(UsersActivity.this, StartActivity.class));
                }
            }

        });
    }

    private void allFiltering(){
        selectedFilters.clear();
        selectedFilters.add("all");
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        unSelectAllFilterButtons();
        lookSelected(allButton);
        UsersList.clear();
        fstore.collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            UsersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                UsersList.add(u);
                            }
                        }
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                    }
                });

    }

    private void NameFilter(SortType type) {
        selectedFilters.clear();
        selectedFilters.add("all");

        unSelectAllFilterButtons();
        lookSelected(allButton);

        UsersList.clear();
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();

        fstore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                UsersList.add(u);
                                if (type != null)
                                    sortList(type);
                                Log.d("allFilterTapped", "UsersList.size=" + UsersList.size());
                                adapter.notifyDataSetChanged();
                                Log.d("allFilterTapped", "UsersList.size=" + UsersList.size());
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                    private void sortList(SortType type) {
                        switch (type) {
                            case UserNAmeDown2Up:
                                Collections.sort(UsersList, User.nameAscending);

                                break;
                            case MessageUp2Down:
                                Collections.sort(UsersList, User.mesAscending);
                                break;
                            default:
                                Log.d("sortList", "default !" + type);
                                break;
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
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        searchView = (SearchView) findViewById(R.id.shapeListSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str)
            {
                UsersList.clear();
                currentSearchText = str;
                Log.d("initSearchWidgets", "str="+str);

                fstore.collection("Users")
                    .orderBy("Email")
                    .startAt(str)
                    .endAt(str + "\uf8ff")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    UsersList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        String email = document.getString("Email");
                                        String id = document.getString("ID");
                                        String user = document.getString("isUser");
                                        String mes = document.getString("LettersNum");
                                        User u = new User(email,id,user,mes);
                                        UsersList.add(u);
                                    }
                                }
                                Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                                adapter.notifyDataSetChanged();
                                Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                            }
                        });
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
                showDetail.putExtra("id",selectUser.getId());
                startActivity(showDetail);
            }
        });
    }

    public void AllTapped(View view)
    {
//        adapter.notifyDataSetChanged();
//        checkForFilter();
        allFiltering();
    }

    public void nameASCTapped(View view)
    {
//        adapter.notifyDataSetChanged();
        NameFilter(SortType.UserNAmeDown2Up);
//        Collections.sort(shapeList, User.nameAscending);
        unSelectAllFilterButtons();
        lookSelected(nameAscButton);
//        adapter.notifyDataSetChanged();
//        setAdapter(shapeList);
    }

    public void messageTapped(View view)
    {

        selectedFilters.clear();
        selectedFilters.add("all");
        FirebaseFirestore fstore;
        fstore = FirebaseFirestore.getInstance();
        unSelectAllFilterButtons();
        lookSelected(messages);
        UsersList.clear();
        fstore.collection("Users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            UsersList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String email = document.getString("Email");
                                String id = document.getString("ID");
                                String user = document.getString("isUser");
                                String mes = document.getString("LettersNum");
                                User u = new User(email,id,user,mes);
                                int num = Integer.parseInt(mes);
                                if ((num>0)) {
                                    UsersList.add(u);
                                }
                            }
                        }
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                        adapter.notifyDataSetChanged();
                        Log.d("initSearchWidgets", "UsersList.size="+UsersList.size());
                    }
                });
        Collections.sort(UsersList, User.mesAscending);

    }



    private void setAdapter(ArrayList<User> UsersList)
    {
        adapter = new UserAdapter(getApplicationContext(), 0, UsersList);
        listView.setAdapter(adapter);
    }
}