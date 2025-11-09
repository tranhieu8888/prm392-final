package com.teamapp.data.repo;

import com.google.gson.Gson;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.api.AuthApi;
import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.dto.AuthDtos.AuthResponse;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthRepository {
    private final AuthApi api;
    private final SessionStore session;
    private final Gson gson = new Gson();

    public AuthRepository(Retrofit retrofit, SessionStore session) {
        this.api = retrofit.create(AuthApi.class);
        this.session = session;
    }

    public AuthResponse login(String email, String password) throws IOException {
        Response<AuthResponse> res = api.login(new AuthDtos.LoginRequest(email, password)).execute();
        if (!res.isSuccessful()) {
            if (res.code() == 401) {
                session.clear();
            }
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        session.saveToken(res.body().token);
        session.saveUserJson(gson.toJson(res.body().user));
        session.saveUserId(res.body().user.id);
        return res.body();
    }

    public AuthResponse register(String fullName, String email, String password) throws IOException {
        Response<AuthResponse> res = api.register(new AuthDtos.RegisterRequest(fullName, email, password)).execute();
        if (!res.isSuccessful()) {
            if (res.code() == 401) {
                session.clear();
            }
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        session.saveToken(res.body().token);
        session.saveUserJson(gson.toJson(res.body().user));
        session.saveUserId(res.body().user.id);
        return res.body();
    }

    public void logout() { session.clear(); }
}
