package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class CommentDtos {

    public static class AddCommentRequest {
        @SerializedName("content") public String content;
        public AddCommentRequest(String content) { this.content = content; }
    }

    public static class CommentDto {
        @SerializedName("id") public UUID id;
        @SerializedName("taskId") public UUID taskId;
        @SerializedName("authorId") public UUID authorId;
        @SerializedName("content") public String content;
        @SerializedName("createdAt") public Date createdAt;
    }
}
