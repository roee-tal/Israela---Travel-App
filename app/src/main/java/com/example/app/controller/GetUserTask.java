//package com.example.app.controller;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.example.app.DetailUserActivity;
//import com.example.app.User;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//
//import java.util.concurrent.ExecutionException;
//
//public class GetUserTask extends AsyncTask<String, Void, User> {
//
//    private DetailUserController detailUserController;
//    private Context context;
//
//    public GetUserTask(DetailUserController detailUserController, Context context) {
//        this.detailUserController = detailUserController;
//        this.context = context;
//    }
//
//    @Override
//    protected User doInBackground(String... params) {
//        String parsedID = params[0];
//        try {
//            return detailUserController.getPUser(parsedID);
//        } catch (ExecutionException | InterruptedException | FirebaseFirestoreException e) {
//            // Handle the exception
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(User user) {
//        if (user != null) {
//            Log.d("ddddddddd", user.getEmail());
//            Intent intent = new Intent(context, DetailUserActivity.class);
//            // Create a Bundle to hold the extra data
//            intent.putExtra("id2",user.getEmail());
//            context.startActivity(intent);
//            // Update the UI with the user data
//        } else {
//            // Handle the case where no user was found
//        }
//    }
//}
