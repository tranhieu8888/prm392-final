package com.teamapp.data.api;

import com.teamapp.data.dto.ConversationDtos;
import com.teamapp.data.dto.MessageDtos;

import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationApi {

    // GET /api/conversations/my
    @GET("/api/conversations/my")
    Call<List<ConversationDtos.ConversationDto>> my(
            @Query("page") int page,
            @Query("pageSize") int size
    );

    // POST /api/conversations/dm
    // Backend có thể trả "uuid-string" => dùng ResponseBody để tự parse
    @POST("/api/conversations/dm")
    Call<ResponseBody> dm(@Body ConversationDtos.StartDmRequest body);

    // POST /api/conversations/group
    // Backend đang trả "uuid-string" => dùng ResponseBody để tự parse
    @POST("/api/conversations/group")
    Call<ResponseBody> group(@Body ConversationDtos.CreateGroupRequest body);

    // GET /api/conversations/{conversationId}/messages
    @GET("/api/conversations/{conversationId}/messages")
    Call<List<MessageDtos.MessageDto>> listMessages(
            @Path("conversationId") UUID conversationId,
            @Query("before") String beforeIso,     // nullable
            @Query("pageSize") Integer pageSize    // nullable
    );

    // POST /api/conversations/{conversationId}/messages
    @POST("/api/conversations/{conversationId}/messages")
    Call<MessageDtos.MessageDto> send(
            @Path("conversationId") UUID conversationId,
            @Body MessageDtos.SendMessageRequest body
    );
}
