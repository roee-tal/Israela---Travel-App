package com.example.app.model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.ContactActivity;
import com.example.app.MainActivity;
import com.example.app.User;
import com.example.app.controller.contactController;
import com.example.app.controller.startController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class contactModel {

    private contactController contcontroller;

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;

    public contactModel(contactController contactontroller){
        this.contcontroller = contactontroller;
    }


    public void bSend(String s) {
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String e_mail = user.getEmail();
        updateMesSize(user.getUid());
        DocumentReference df = fstore.collection("Messages").document();
        Map<String,Object> us_info = new HashMap<>();
        us_info.put("ID", user.getUid());
        us_info.put("Email", e_mail);
        us_info.put("Message", s);
        us_info.put("mesId",df.getId());
        df.set(us_info);
        contcontroller.showToast("message sent");
        contcontroller.showMainActivity();
//        Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(ContactActivity.this, MainActivity.class));
//        finish();
    }

    private void updateMesSize(String id){
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    String email = doc.getString("Email");
                    String id = doc.getString("ID");
                    String user = doc.getString("isUser");
                    String num = doc.getString("LettersNum");
                    int num_cov = Integer.parseInt(num);
                    num_cov++;
                    num = Integer.toString(num_cov);
                    User user1 = new User(email,id,user,num);
                    fstore.collection("Users").document(id).update("LettersNum",num);

                }
            }
        });
    }
}
