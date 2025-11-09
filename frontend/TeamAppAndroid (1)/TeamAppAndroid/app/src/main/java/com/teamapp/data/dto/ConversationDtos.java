// app/src/main/java/com/teamapp/data/dto/ConversationDtos.java
package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class ConversationDtos {

    public static class ConversationDto {
        @SerializedName("id")        public UUID id;
        @SerializedName("projectId") public UUID projectId; // có thể null nếu DM
        @SerializedName("type")      public String type;    // "dm" | "group"
        @SerializedName("title")     public String title;
        @SerializedName("createdAt") public java.util.Date createdAt;
        @SerializedName("members")   public List<ConversationMemberDto> members;
    }

    public static class ConversationMemberDto {
        @SerializedName("userId")            public UUID userId;
        @SerializedName("lastReadMessageId") public UUID lastReadMessageId;
        @SerializedName("lastReadAt")        public java.util.Date lastReadAt;
    }

    // POST /api/conversations/dm
    public static class StartDmRequest {
        @SerializedName("otherUserId") public UUID otherUserId;
        public StartDmRequest(UUID otherUserId) { this.otherUserId = otherUserId; }
    }

    // POST /api/conversations/group
    public static class CreateGroupRequest {
        @SerializedName("projectId") public UUID projectId;      // optional theo backend
        @SerializedName("title")     public String title;
        @SerializedName("memberIds") public List<UUID> memberIds;// optional

        public CreateGroupRequest(UUID projectId, String title, List<UUID> memberIds) {
            this.projectId = projectId;
            this.title = title;
            this.memberIds = memberIds;
        }
    }

    // ⬅️ Backend trả { conversationId }
    public static class CreateGroupResponse {
        @SerializedName("conversationId") public UUID conversationId;
    }
}
