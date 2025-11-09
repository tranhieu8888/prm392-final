// app/src/main/java/com/teamapp/data/repo/NotificationRepository.java
package com.teamapp.data.repo;

import androidx.annotation.WorkerThread;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.NotificationApi;
import com.teamapp.data.dto.NotificationDtos;
import com.teamapp.data.entity.NotificationEntity;
import com.teamapp.data.mapper.Mapper;

import java.util.List;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationRepository {
    private final NotificationApi api;
    private final AppDb db;

    public NotificationRepository(Retrofit retrofit, AppDb db) {
        this.api = retrofit.create(NotificationApi.class);
        this.db = db;
    }

    /** Đọc cache cục bộ để hiển thị tức thì */
    @WorkerThread
    public List<NotificationEntity> recentLocal() {
        return db.notificationDao().recent();
    }

    /** Gọi server, upsert vào Room, trả list mới nhất */
    @WorkerThread
    public List<NotificationEntity> fetchAll() throws Exception {
        Response<List<NotificationDtos.NotificationDto>> res = api.list().execute();
        if (!res.isSuccessful() || res.body() == null) throw new Exception("Không tải thông báo");
        List<NotificationEntity> entities = Mapper.toNotificationEntities(res.body());
        db.notificationDao().upsertAll(entities);
        return entities;
    }

    /** Fallback: nếu mạng lỗi → trả cache để UI vẫn có dữ liệu */
    @WorkerThread
    public List<NotificationEntity> fetchAllSafe() throws Exception {
        try {
            return fetchAll();
        } catch (Exception ex) {
            List<NotificationEntity> cached = db.notificationDao().recent();
            if (cached != null && !cached.isEmpty()) return cached;
            throw ex;
        }
    }

    /** Đánh dấu 1 thông báo đã đọc (gọi API + cập nhật Room) */
    @WorkerThread
    public void mark(UUID id, boolean isRead) throws Exception {
        Response<Void> res = api.mark(id, new NotificationDtos.MarkReadRequest(isRead)).execute();
        if (!res.isSuccessful()) throw new Exception("Không cập nhật thông báo");

        // sync local
        NotificationEntity n = db.notificationDao().findById(id);
        if (n != null) {
            n.isRead = isRead;
            db.notificationDao().upsert(n);
        } else {
            // nếu chưa có local, fetch lại list
            fetchAllSafe();
        }
    }

    /** Đánh dấu tất cả là đã đọc — nếu backend chưa có endpoint, lặp từng cái */
    @WorkerThread
    public void markAllRead() throws Exception {
        // Cách 1: lặp từng item gọi API mark (đảm bảo đúng nguồn sự thật)
        List<NotificationEntity> list = db.notificationDao().recent();
        if (list == null) return;
        for (NotificationEntity n : list) {
            if (!n.isRead) mark(n.id, true);
        }
        // Cách 2 (nhanh cục bộ): mark tất cả local — chỉ dùng khi backend không yêu cầu chính xác real-time
        // db.notificationDao().markAllReadLocal();
    }

    /** Số lượng chưa đọc — dùng cho badge */
    @WorkerThread
    public int unreadCountLocal() {
        return db.notificationDao().countUnread();
    }
}
