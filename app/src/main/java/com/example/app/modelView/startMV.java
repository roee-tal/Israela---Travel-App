package com.example.app.modelView;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.app.R;
import com.example.app.activities.AdminActivity;
import com.example.app.activities.LoginActivity;
import com.example.app.activities.MainActivity;
import com.example.app.activities.StartActivity;
import com.example.app.helpClasses.ShowToastAndSignOut;
import com.example.app.model.api.appServer;
import com.example.app.model.startModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class startMV {

    private FirebaseAuth auth;
    private startModel startmodel;
    private StartActivity view;
    private ShowToastAndSignOut showToastAndSignOut;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    public startMV(StartActivity view){
        this.view = view;
        startmodel = new startModel(this);
        showToastAndSignOut = new ShowToastAndSignOut();
        auth = FirebaseAuth.getInstance();

    }

    //Insert via mail and password
    public void bLoginMail(String mail, String password) {
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)){
            showToast("Empty Credentials!");
        }
        else {
            startmodel.notBlocked(mail, password);
        }
//        startmodel.bLoginMail(mail, password);
    }

    //Insert via google
    public void bLoginG(Activity activity)
    {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(activity.getString(R.string.default_web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = gsc.getSignInIntent();
        activity.startActivityForResult(signInIntent,1000);
    }

    public void showUserActivity() {
        view.startActivity(new Intent(view, MainActivity.class));
        view.finish();
    }

    public void showAdminActivity() {
        view.startActivity(new Intent(view, AdminActivity.class));
        view.finish();
    }

    public void showToast(String message) {
        showToastAndSignOut.showToast(view, message);
    }

    public void activityRes(int requestCode, int resultCode, Intent data) {
        startmodel.activityRes(requestCode, resultCode, data);
    }

    public void showLoginActivity() {
        view.startActivity(new Intent(view, LoginActivity.class));
        view.finish();
    }

    /**
     * This function Checks if the account is a user or admin.
     * This done by app server
     * @param uid The id of the account in firebase
     */
    public void connectToServer(String uid) {
        appServer.checkUserAccessLevel(uid, this);

    }
        // For login with mail and password
        public void loginU(String txt_email, String pas_email) {
            auth.signInWithEmailAndPassword(txt_email, pas_email).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        // If the email is verified check if the account is for user or admin via the server app
                        if(user.isEmailVerified()) {
                            showToast("welcome");
                            view.startActivity(new Intent(view, AdminActivity.class)); // Debug Mode
                            view.finish(); // Debug Mode
//                            connectToServer(user.getUid());
                        }
                        else{
                            showToast("Please verify your email");
                        }
                    }
                    else {
                        showToast("Wrong password or email");
                    }
                }
            });
        }

    public void firebaseauthwithgoogle(GoogleSignInAccount account) {
        AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(cred).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = auth.getCurrentUser();
                String uid = user.getUid();
                String e_mail = user.getEmail();
                if (authResult.getAdditionalUserInfo().isNewUser()) {
                    // Insert the user details to firebase
                    startmodel.firebaseauthwithgoogle(uid,e_mail);
                }
                else {
                    showToast("Welcome");
                }
                // check for user and admin via the server
                connectToServer(uid);
            }
        });
    }
}

