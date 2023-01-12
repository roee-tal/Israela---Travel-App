package com.example.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.helpClasses.BottomNavActivity;
import com.example.app.R;
import com.example.app.modelView.adapters.MessageAdapter;
import com.example.app.modelView.ContactAdminMV;
import com.example.app.model.objects.Message;

import java.util.ArrayList;

public class ContactAdminActivity extends AppCompatActivity
{

    public static ArrayList<Message> messList = new ArrayList<Message>();

    private ListView listView;
    private TextView email;

    private MessageAdapter adapter;
    private ContactAdminMV contactAdminMV;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_admin);
        email = findViewById(R.id.email_con);

        //Initialize MV of this activity
        contactAdminMV = new ContactAdminMV(this,adapter);

        // For the menu bar in the bottom
        clickOnBottomNav();

        setUpList(); //By adapter

        getSelectedMessage();

        setUpOnclickListener();

    }


    private void clickOnBottomNav(){
        BottomNavActivity bottomNavActivity = new BottomNavActivity();
        bottomNavActivity.bNav(this);

    }

    /**
     * Get the email of the user from the previous activity
     * Show his messages with help function
     */
    private void getSelectedMessage()
    {
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        // use the text in a TextView
        TextView textView = (TextView) findViewById(R.id.email_con);
        textView.setText(text);
        setupData(text);
    }

    private void setupData(String text)
    {
        contactAdminMV.showList(adapter,text,messList);
    }

    // Initialize the list
    private void setUpList()
    {
        listView = (ListView) findViewById(R.id.shapesListView);

        setAdapter(messList);
    }

    // Click on a message
    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                // Move activity and send there relevant data
                String s = email.getText().toString();
                Message selectMess = (Message) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailMessageActivity.class);
                showDetail.putExtra(Intent.EXTRA_TEXT,selectMess.getText());
                showDetail.putExtra("mail",s);
                showDetail.putExtra("id",selectMess.getid());
                startActivity(showDetail);
                finish();
            }
        });
    }

    // When tap on 'messages'
    public void messageTapped(View view)
    {
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        setupData(text);
    }


    private void setAdapter(ArrayList<Message> messList)
    {
        adapter = new MessageAdapter(getApplicationContext(), 0, messList);
        listView.setAdapter(adapter);
    }

    public void notifyAdapter(MessageAdapter adapter) {
        adapter.notifyDataSetChanged();
    }
}
