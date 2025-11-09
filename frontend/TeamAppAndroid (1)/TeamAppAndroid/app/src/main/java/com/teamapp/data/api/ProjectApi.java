package com.teamapp.data.api;



import com.teamapp.data.dto.MemberDtos;
import com.teamapp.data.dto.ProjectDtos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// ProjectApi: OK (body invite cần key "email")
public interface ProjectApi {
    @GET("api/projects/my")
    Call<List<ProjectDtos.ProjectDto>> my();

    @POST("api/projects")
    Call<ProjectDtos.ProjectDto> create(@Body ProjectDtos.CreateProjectRequest req);

    @GET("api/projects/{id}/members")
    Call<List<MemberDtos.MemberDto>> members(@Path("id") UUID projectId);

    @POST("api/projects/{id}/members")
    Call<Void> invite(@Path("id") UUID projectId, @Body Map<String, String> body);

    @GET("api/projects/discover")
    Call<List<ProjectDtos.ProjectDto>> discover(@Query("query") String q);
}

