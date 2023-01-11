package com.example.app.model.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {


    @GET("findIsUser")
    Call<String> findIsUser(
            @Query("uid") String uid
    );

    @FormUrlEncoded
    @POST("createUser")
    Call<ResponseBody> createUser
            (
            @Field("ID") String ID,
            @Field("Email") String Email,
            @Field("isUser") String isUser,
            @Field("LettersNum") String LettersNum
    );
}
