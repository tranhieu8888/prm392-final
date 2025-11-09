package com.teamapp.data.api;



import com.teamapp.data.dto.MessageDtos;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// MessageApi: SỬA limit -> pageSize
public interface MessageApi {
    @GET("api/conversations/{conversationId}/messages")
    Call<List<MessageDtos.MessageDto>> list(
            @Path("conversationId") UUID conversationId,
            @Query("before") String beforeIso,
            @Query("pageSize") int pageSize
    );

    @POST("api/conversations/{conversationId}/messages")
    Call<MessageDtos.MessageDto> send(@Path("conversationId") UUID conversationId, @Body MessageDtos.SendMessageRequest req);
}

