package com.example.app.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.model.objects.Message;
import com.example.app.modelView.adapters.MessageAdapter;
import com.example.app.modelView.ContactAdminMV;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactAdminModel {

    private FirebaseFirestore fstore;
    ContactAdminMV contactAdminMV;

    public ContactAdminModel(ContactAdminMV contactAdminMV) {
        this.contactAdminMV = contactAdminMV;
        fstore = FirebaseFirestore.getInstance();
    }

    /**
     * Get the messages of the user with help of his email
     * Then , notify the adapter with help function
     * @param adapter the message adapter
     * @param text the mail of the user
     * @param messList insert his messages to ArrayList 'messList'
     */
    public void showList(MessageAdapter adapter, String text, ArrayList<Message> messList) {
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
                                messList.add(m);
                            }
                            contactAdminMV.notifyAdapter(adapter);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
          });
    }
}
