package com.example.app.model.api;

import com.example.app.modelView.startMV;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class appServer {

    /**
     * this is our function that uses our app server.
     * check the auth of the user in order to secure that user wont get to admin screens and back
     * @param uid id of the user
     * @param controller to move the matching activity
     */
    public static void checkUserAccessLevel(String uid, startMV controller) {
        Call<String> call = RegisterAPI.getInstance().getAPI().findIsUser(uid); // Contact the server with API
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) { // When the server gives response
                if(response.isSuccessful()) {
                    String s = response.body();
                    if (s.equals("1")){ // If user
                        controller.showUserActivity();
                    }
                    else{
                        controller.showAdminActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
