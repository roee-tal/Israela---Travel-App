package com.example.app.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.DetailMessageActivity;
import com.example.app.UsersActivity;
import com.example.app.controller.DetailMessageController;
import com.example.app.controller.ShowToastAndSignOut;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailMessageModel {

    private FirebaseFirestore fstore;
    DetailMessageController detailMessageController;
    ShowToastAndSignOut showToastAndSignOut;

    public DetailMessageModel(DetailMessageController detailMessageController) {
        this.detailMessageController = detailMessageController;
        showToastAndSignOut = new ShowToastAndSignOut();
    }

    public void bDelete(String parsedMail) {
        fstore = FirebaseFirestore.getInstance();
        Log.d("SSSS",parsedMail);
            fstore.collection("Messages").document(parsedMail)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                String uId = document.getString("ID");
                                String uuId = document.getString("mesId");
                                Log.d("String uIdNamess", "id="+uId);
                                detailMessageController.areYouSureMessage(parsedMail,uId);
                            }
                        }
                    });
        }

    public void bAreYouSureLogic(Activity activity, String parsedMail, String id) {
        del(parsedMail);
        fstore = FirebaseFirestore.getInstance();

//        Toast.makeText(activity, "message removed!", Toast.LENGTH_SHORT).show();
        showToastAndSignOut.showToast(activity,"message removed!");
        detailMessageController.ShowUsers();
        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String s = document.getString("LettersNum");
                    int num = Integer.parseInt(s);
                    num--;
                    s = Integer.toString(num);
                    fstore.collection("Users").document(id).update("LettersNum", s);
                }
            }
        });
    }

    private void del(String parsedMail) {
        fstore.collection("Messages").document(parsedMail).delete();
    }
}
