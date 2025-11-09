package com.teamapp.data.repo;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.ProjectApi;
import com.teamapp.data.dto.MemberDtos;
import com.teamapp.data.dto.ProjectDtos;
import com.teamapp.data.entity.ProjectEntity;
import com.teamapp.data.mapper.Mapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

public class ProjectRepository {
    private final ProjectApi api;
    private final AppDb db;

    public ProjectRepository(Retrofit retrofit, AppDb db) {
        this.api = retrofit.create(ProjectApi.class);
        this.db = db;
    }

    public void discoverAsync(String query, retrofit2.Callback<List<ProjectDtos.ProjectDto>> cb) {
        api.discover(query).enqueue(cb);
    }

    public List<ProjectDtos.ProjectDto> discoverSync(String q) throws IOException {
        Response<List<ProjectDtos.ProjectDto>> res = api.discover(q).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        return res.body();
    }

    public void fetchMyProjects() throws IOException {
        Response<List<ProjectDtos.ProjectDto>> res = api.my().execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        List<ProjectEntity> entities = Mapper.toProjectEntities(res.body());
        db.projectDao().upsertAll(entities);
    }

    public ProjectEntity create(String name, String desc, boolean isPublic) throws IOException {
        Response<ProjectDtos.ProjectDto> res = api.create(new ProjectDtos.CreateProjectRequest(name, desc, isPublic)).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        ProjectEntity e = Mapper.toProjectEntity(res.body());
        db.projectDao().upsert(e);
        return e;
    }

    // ------ mở rộng đầy đủ API ------


    public void invite(UUID projectId, String email) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        Response<Void> res = api.invite(projectId, body).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
    }


    /** DISCOVER: dùng ở DiscoverProjectsActivity */
    public List<ProjectDtos.ProjectDto> discover(String query) throws IOException {
        Response<List<ProjectDtos.ProjectDto>> res = api.discover(query == null ? "" : query).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        return res.body();
    }

    /** MEMBERS: dùng ở MembersActivity */
    public List<MemberDtos.MemberDto> members(UUID projectId) throws IOException {
        Response<List<MemberDtos.MemberDto>> res = api.members(projectId).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
        if (res.body() == null) {
            throw new IOException("Response body is null");
        }
        return res.body();
    }

    /** INVITE BY EMAIL: dùng ở MembersActivity */
    public void inviteByEmail(UUID projectId, String email) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        Response<Void> res = api.invite(projectId, body).execute();
        if (!res.isSuccessful()) {
            throw new IOException("API Error: " + res.code() + " " + res.message());
        }
    }
}
