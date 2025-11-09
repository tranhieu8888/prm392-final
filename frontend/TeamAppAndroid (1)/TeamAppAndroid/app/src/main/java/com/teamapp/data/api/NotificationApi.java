package com.teamapp.data.api;



import com.teamapp.data.dto.NotificationDtos;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;


// NotificationApi: OK
public interface NotificationApi {
    @GET("api/notifications")
    Call<List<NotificationDtos.NotificationDto>> list();

    @PATCH("api/notifications/{id}")
    Call<Void> mark(@Path("id") UUID id, @Body NotificationDtos.MarkReadRequest req);
}


