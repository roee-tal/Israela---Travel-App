package com.example.app.model;

import androidx.annotation.NonNull;

import com.example.app.modelView.helpClassesMV.ShowToastAndSignOut;
import com.example.app.modelView.contactMV;
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

    private contactMV contactMV;

    private FirebaseAuth auth;
    private FirebaseFirestore fstore;
    ShowToastAndSignOut showToastAndSignOut;

    public contactModel(contactMV contactontroller){
        this.contactMV = contactontroller;
        showToastAndSignOut = new ShowToastAndSignOut();
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
    }

    // Sends a message by upload it to the database
    public void bSend(String s) {
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
        contactMV.showToast("message sent");
        contactMV.showMainActivity();
    }

    /** increase the field 'LettersNum' for the user in the database by 1
     * @param id
     */
    private void updateMesSize(String id){
        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    String id = doc.getString("ID");
                    String num = doc.getString("LettersNum");
                    int num_cov = Integer.parseInt(num);
                    num_cov++;
                    num = Integer.toString(num_cov);
                    fstore.collection("Users").document(id).update("LettersNum",num);
                }
            }
        });
    }
}
