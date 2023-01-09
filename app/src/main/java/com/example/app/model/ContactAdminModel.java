package com.example.app.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.Message;
import com.example.app.MessageAdapter;
import com.example.app.controller.ContactAdminController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactAdminModel {

    private FirebaseFirestore fstore;
    ContactAdminController contactAdminController;

    public ContactAdminModel(ContactAdminController contactAdminController) {
        this.contactAdminController = contactAdminController;
    }

    public void showList(MessageAdapter adapter, String text, ArrayList<Message> messList) {
        fstore = FirebaseFirestore.getInstance();
        messList.clear();

        fstore.collection("Messages").whereEqualTo("Email",text)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            messList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String mess = document.getString("Message");
                                String id = document.getString("mesId");
                                Message m = new Message(mess,id);
                                Log.d("TAGGGG", m.getText());
                                Log.d("TAGGGGID", m.getid());
                                messList.add(m);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
