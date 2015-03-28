package com.nicktardif.seniorproject.universalhackathon;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by tick on 3/28/15.
 */
public interface MythosService {
    @FormUrlEncoded
    @POST("/uuid")
    void getUUID(@Field("api_key") String api_key, Callback<GetUUIDResponse> res);
}
