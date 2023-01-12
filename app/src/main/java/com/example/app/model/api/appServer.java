package com.example.app.model.api;

import com.example.app.modelView.startMV;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class appServer {


    public static void checkUserAccessLevel(String uid, startMV controller) {
        Call<String> call = RegisterAPI.getInstance().getAPI().findIsUser(uid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String s = response.body();
                    if (s.equals("1")){
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
