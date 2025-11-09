package com.teamapp.data.api;

import com.teamapp.data.dto.ProfileDtos;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileApi {

    /** GET /api/me  -> trả về thông tin người dùng hiện tại */
    @GET("api/me")
    Call<ProfileDtos.UserDto> me();

    /** PUT /api/me/profile  -> cập nhật họ tên + avatar */
    @PUT("api/me/profile")
    Call<Void> updateProfile(@Body ProfileDtos.UpdateProfileRequest body);

    /** PUT /api/me/password -> đổi mật khẩu */
    @PUT("api/me/password")
    Call<Void> changePassword(@Body ProfileDtos.ChangePasswordRequest body);
}
