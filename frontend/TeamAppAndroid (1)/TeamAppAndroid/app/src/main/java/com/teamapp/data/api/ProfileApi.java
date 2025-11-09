// com/teamapp/data/api/ProfileApi.java
package com.teamapp.data.api;

import com.teamapp.data.dto.ProfileDtos;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

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
