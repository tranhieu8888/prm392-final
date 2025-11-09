package com.teamapp.data.api;



import com.teamapp.data.dto.DeviceDtos;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// DeviceApi: GIỮ LẠI (body). XÓA interface DevicesApi (query) để tránh nhầm.
public interface DeviceApi {
    @POST("api/devices")
    Call<Void> save(@Body DeviceDtos.SaveDeviceTokenRequest req);
}

