package com.teamapp.data.api;



import com.teamapp.data.dto.CommentDtos;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// CommentApi: thêm tuỳ chọn phân trang (không bắt buộc)
public interface CommentApi {
    @GET("api/tasks/{taskId}/comments")
    Call<List<CommentDtos.CommentDto>> list(
            @Path("taskId") UUID taskId
    );

    @POST("api/tasks/{taskId}/comments")
    Call<CommentDtos.CommentDto> add(@Path("taskId") UUID taskId, @Body CommentDtos.AddCommentRequest req);
}

