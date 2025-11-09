// com/teamapp/data/repo/SearchRepository.java
package com.teamapp.data.repo;

import com.teamapp.data.api.SearchApi;
import com.teamapp.data.dto.SearchDtos.SearchResultDto;

import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchRepository {
    private final SearchApi api;

    public SearchRepository(Retrofit retrofit) {
        this.api = retrofit.create(SearchApi.class);
    }

    public SearchResultDto search(String q) throws Exception {
        Response<SearchResultDto> res = api.global(q).execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không tìm thấy kết quả");
        return res.body();
    }
}
