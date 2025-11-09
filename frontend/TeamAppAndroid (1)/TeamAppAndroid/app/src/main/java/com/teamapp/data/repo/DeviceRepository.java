// com/teamapp/data/repo/DeviceRepository.java
package com.teamapp.data.repo;

import com.teamapp.data.api.DeviceApi;
import com.teamapp.data.dto.DeviceDtos;

import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceRepository {
    private final DeviceApi api;

    public DeviceRepository(Retrofit retrofit) {
        this.api = retrofit.create(DeviceApi.class);
    }

    public void saveFcmToken(String token, String platform) throws Exception {
        Response<Void> res = api.save(new DeviceDtos.SaveDeviceTokenRequest(token, platform)).execute();
        if (!res.isSuccessful()) throw new Exception("Lưu FCM token thất bại");
    }
}
