// com/teamapp/data/repo/CommentRepository.java
package com.teamapp.data.repo;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.CommentApi;
import com.teamapp.data.dto.CommentDtos;
import com.teamapp.data.entity.CommentEntity;
import com.teamapp.data.mapper.Mapper;

import java.util.List;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentRepository {
    private final CommentApi api;
    private final AppDb db;

    public CommentRepository(Retrofit retrofit, AppDb db) {
        this.api = retrofit.create(CommentApi.class);
        this.db = db;
    }

    public List<CommentEntity> fetchByTask(UUID taskId) throws Exception {
        Response<List<CommentDtos.CommentDto>> res = api.list(taskId).execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không tải được bình luận");
        List<CommentEntity> entities = Mapper.toCommentEntities(res.body());
        db.commentDao().upsertAll(entities);
        return entities;
    }

    public CommentEntity add(UUID taskId, String content) throws Exception {
        Response<CommentDtos.CommentDto> res = api.add(taskId, new CommentDtos.AddCommentRequest(content)).execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không thêm được bình luận");
        CommentEntity e = Mapper.toCommentEntity(res.body());
        db.commentDao().upsert(e);
        return e;
    }
}
