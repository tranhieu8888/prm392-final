package com.teamapp.data.api;



import com.teamapp.data.dto.SearchDtos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// SearchApi: OK
public interface SearchApi {
    @GET("api/search")
    Call<SearchDtos.SearchResultDto> global(@Query("q") String query);
}

