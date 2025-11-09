// com/teamapp/data/repo/JoinRequestRepository.java
package com.teamapp.data.repo;

import com.teamapp.data.api.JoinRequestApi;
import com.teamapp.data.dto.JoinRequestDtos;

import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

public class JoinRequestRepository {
    private final JoinRequestApi api;

    public JoinRequestRepository(Retrofit retrofit) {
        this.api = retrofit.create(JoinRequestApi.class);
    }

    public JoinRequestDtos.JoinRequestDto request(UUID projectId) throws Exception {
        Response<JoinRequestDtos.JoinRequestDto> res = api.request(projectId).execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Gửi yêu cầu tham gia thất bại");
        return res.body();
    }

    public void decide(UUID joinRequestId, boolean approve) throws Exception {
        Response<Void> res = api.decide(joinRequestId, new JoinRequestDtos.ApproveJoinRequestRequest(approve)).execute();
        if (!res.isSuccessful()) throw new Exception("Duyệt yêu cầu thất bại");
    }
}
