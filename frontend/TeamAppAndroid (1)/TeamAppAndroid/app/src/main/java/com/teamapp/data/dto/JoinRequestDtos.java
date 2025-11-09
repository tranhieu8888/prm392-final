package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class JoinRequestDtos {

    public static class JoinRequestDto {
        @SerializedName("id") public UUID id;
        @SerializedName("projectId") public UUID projectId;
        @SerializedName("requesterId") public UUID requesterId;
        @SerializedName("status") public String status;
        @SerializedName("createdAt") public Date createdAt;
    }

    public static class ApproveJoinRequestRequest {
        @SerializedName("approve") public boolean approve;
        public ApproveJoinRequestRequest(boolean approve) { this.approve = approve; }
    }
}
