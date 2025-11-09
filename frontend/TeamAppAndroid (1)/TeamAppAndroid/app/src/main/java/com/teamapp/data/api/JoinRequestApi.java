package com.teamapp.data.api;



import com.teamapp.data.dto.JoinRequestDtos;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

// JoinRequestApi: OK
public interface JoinRequestApi {
    @POST("api/join-requests/{projectId}")
    Call<JoinRequestDtos.JoinRequestDto> request(@Path("projectId") UUID projectId);

    @POST("api/join-requests/{joinRequestId}/decision")
    Call<Void> decide(@Path("joinRequestId") UUID joinRequestId, @Body JoinRequestDtos.ApproveJoinRequestRequest req);
}

