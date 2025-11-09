// com/teamapp/data/repo/CalendarRepository.java
package com.teamapp.data.repo;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.CalendarApi;
import com.teamapp.data.dto.TaskDtos;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.mapper.Mapper;

import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

public class CalendarRepository {
    private final CalendarApi api;
    private final AppDb db;

    public CalendarRepository(Retrofit retrofit, AppDb db) {
        this.api = retrofit.create(CalendarApi.class);
        this.db = db;
    }

    public List<TaskEntity> tasksInMonth(int year, int month) throws Exception {
        Response<List<TaskDtos.TaskDto>> res = api.tasksInMonth(year, month).execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không tải lịch công việc");
        List<TaskEntity> list = Mapper.toTaskEntities(res.body());
        db.taskDao().upsertAll(list);
        return list;
    }
}
