package com.teamapp.data.api;

import com.teamapp.data.dto.AuthDtos;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// AuthApi: OK
public interface AuthApi {
    @POST("api/auth/register")
    Call<AuthDtos.AuthResponse> register(@Body AuthDtos.RegisterRequest req);

    @POST("api/auth/login")
    Call<AuthDtos.AuthResponse> login(@Body AuthDtos.LoginRequest req);
}

