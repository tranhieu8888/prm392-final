package com.teamapp.data.api;



import com.teamapp.data.dto.TaskDtos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// CalendarApi: OK
public interface CalendarApi {
    @GET("api/calendar/tasks")
    Call<List<TaskDtos.TaskDto>> tasksInMonth(@Query("year") int year, @Query("month") int month);
}

