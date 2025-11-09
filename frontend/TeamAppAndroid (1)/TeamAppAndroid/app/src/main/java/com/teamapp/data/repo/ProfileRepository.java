package com.teamapp.data.repo;

import com.google.gson.Gson;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.api.ProfileApi;
import com.teamapp.data.dto.ProfileDtos;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileRepository {
    private final ProfileApi api;
    private final SessionStore session;
    private final Gson gson = new Gson();

    public ProfileRepository(Retrofit retrofit, SessionStore session) {
        this.api = retrofit.create(ProfileApi.class);
        this.session = session;
    }

    public ProfileDtos.UserDto me() throws Exception {
        Response<ProfileDtos.UserDto> res = api.me().execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không lấy được thông tin tài khoản");
        session.saveUser(res.body()); // Use the new saveUser method
        session.saveUserId(res.body().id);
        return res.body();
    }

    public Response<Void> updateProfile(ProfileDtos.UpdateProfileRequest body) throws Exception {
        Response<Void> res = api.updateProfile(body).execute();
        if (!res.isSuccessful()) throw new Exception("Cập nhật hồ sơ thất bại");
        return res;
    }

    public void changePassword(String currentPwd, String newPwd) throws Exception {
        Response<Void> res = api.changePassword(new ProfileDtos.ChangePasswordRequest(currentPwd, newPwd)).execute();
        if (!res.isSuccessful()) throw new Exception("Đổi mật khẩu thất bại");
    }
}
