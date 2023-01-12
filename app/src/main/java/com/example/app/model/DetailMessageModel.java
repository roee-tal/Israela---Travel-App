package com.example.app.model;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.modelView.DetailMessageMV;
import com.example.app.modelView.helpClasses.ShowToastAndSignOut;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailMessageModel {

    private FirebaseFirestore fstore;
    DetailMessageMV detailMessageMV;
    ShowToastAndSignOut showToastAndSignOut;

    public DetailMessageModel(DetailMessageMV detailMessageController) {
        this.detailMessageMV = detailMessageController;
        showToastAndSignOut = new ShowToastAndSignOut();
        fstore = FirebaseFirestore.getInstance();
    }

    /**
     * get the matching message
     * @param parsedStringID The message ID
     */
    public void bDelete(String parsedStringID) {
            fstore.collection("Messages").document(parsedStringID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                String uId = document.getString("ID");
                                String uuId = document.getString("mesId");
                                Log.d("String uIdNamess", "id="+uId);
                                detailMessageMV.areYouSureMessage(parsedStringID,uId);
                            }
                        }
                    });
        }


    public void bAreYouSureLogic(Activity activity, String parsedMail, String id) {
        //Delete the message
        del(parsedMail);
        showToastAndSignOut.showToast(activity,"message removed!");
        detailMessageMV.ShowUsers();
        fstore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Decrease 'lettersNum' of the user by 1
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
