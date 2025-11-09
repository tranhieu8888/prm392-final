// com/teamapp/data/repo/ProfileRepository.java
package com.teamapp.data.repo;

import com.google.gson.Gson;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.api.ProfileApi;
import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.dto.ProfileDtos;

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
        session.saveUserJson(gson.toJson(res.body()));
        session.saveUserId(res.body().id);
        return res.body();
    }

    public void updateProfile(String fullName, String avatarUrl) throws Exception {
        Response<Void> res = api.updateProfile(new ProfileDtos.UpdateProfileRequest(fullName, avatarUrl)).execute();
        if (!res.isSuccessful()) throw new Exception("Cập nhật hồ sơ thất bại");
    }

    public void changePassword(String currentPwd, String newPwd) throws Exception {
        Response<Void> res = api.changePassword(new ProfileDtos.ChangePasswordRequest(currentPwd, newPwd)).execute();
        if (!res.isSuccessful()) throw new Exception("Đổi mật khẩu thất bại");
    }
}
