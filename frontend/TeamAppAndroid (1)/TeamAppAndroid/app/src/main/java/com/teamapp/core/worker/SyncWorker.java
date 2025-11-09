package com.teamapp.core.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamapp.App;
import com.teamapp.core.db.AppDb;
import com.teamapp.core.db.dao.MessageDao;
import com.teamapp.core.db.dao.NotificationDao;
import com.teamapp.core.db.dao.PendingActionDao;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.core.realtime.SignalRClient;
import com.teamapp.data.api.CommentApi;
import com.teamapp.data.api.ConversationApi;
import com.teamapp.data.api.MessageApi;
import com.teamapp.data.api.NotificationApi;
import com.teamapp.data.api.TaskApi;

import com.teamapp.data.dto.CommentDtos;
import com.teamapp.data.dto.ConversationDtos;
import com.teamapp.data.dto.MessageDtos;
import com.teamapp.data.dto.NotificationDtos;
import com.teamapp.data.dto.TaskDtos;
import com.teamapp.data.entity.MessageEntity;
import com.teamapp.data.entity.NotificationEntity;
import com.teamapp.data.entity.PendingActionEntity;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.mapper.Mapper;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Đồng bộ offline:
 * 1) Đẩy hàng đợi pending_actions (ADD_COMMENT, UPDATE_TASK_STATUS, SEND_MESSAGE, ...)
 * 2) Kéo dữ liệu mới (ví dụ notifications, messages mới).
 *
 * Thiết kế tối giản để bạn cắm thêm case tuỳ ý.
 */
public class SyncWorker extends Worker {

    private static final int MAX_RETRY = 5;

    private final AppDb db;
    private final SessionStore session;
    private final Retrofit retrofit;
    private final Gson gson;

    private final PendingActionDao pendingDao;
    private final NotificationDao notificationDao;
    private final MessageDao messageDao;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        App app = App.get();
        this.db = app.db();
        this.session = app.session();
        this.retrofit = app.retrofit();
        this.gson = app.gson();

        this.pendingDao = db.pendingActionDao();
        this.notificationDao = db.notificationDao();
        this.messageDao = db.messageDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (session.getToken() == null) return Result.success(); // chưa login thì bỏ qua

            pushPendingActions();
            pullNotifications();
            pullRecentConversationsAndMessages();
            // bạn có thể thêm pull tasks delta nếu server có endpoint hỗ trợ

            return Result.success();
        } catch (Exception e) {
            // Có lỗi bất ngờ: để WorkManager thử lại theo lịch
            return Result.retry();
        }
    }

    /* ===================== PUSH PENDING ===================== */

    private void pushPendingActions() {
        List<PendingActionEntity> pending = pendingDao.queued();
        if (pending == null || pending.isEmpty()) return;

        for (PendingActionEntity act : pending) {
            boolean success = false;
            try {
                switch (act.actionType) {
                    case "ADD_COMMENT":
                        success = handleAddComment(act.payloadJson);
                        break;
                    case "UPDATE_TASK_STATUS":
                        success = handleUpdateTaskStatus(act.payloadJson);
                        break;
                    case "SEND_MESSAGE":
                        success = handleSendMessage(act.payloadJson);
                        break;
                    // case "CREATE_TASK": ...
                    // case "MARK_NOTIFICATION_READ": ...
                    default:
                        // chưa hỗ trợ → bỏ qua (hoặc xóa để không kẹt hàng đợi)
                        success = true;
                        break;
                }
            } catch (Exception ex) {
                success = false;
            }

            if (success) {
                pendingDao.delete(act.id);
            } else {
                act.retryCount = act.retryCount + 1;
                if (act.retryCount > MAX_RETRY) {
                    // quá số lần thử → bỏ qua để không chặn các pending sau
                    pendingDao.delete(act.id);
                } else {
                    pendingDao.upsert(act);
                }
            }
        }
    }

    private boolean handleAddComment(String payloadJson) throws Exception {
        // payload: { "taskId":"...", "content":"..." }
        Type t = new TypeToken<AddCommentPayload>(){}.getType();
        AddCommentPayload p = gson.fromJson(payloadJson, t);
        if (p == null || p.taskId == null || p.content == null || p.content.trim().isEmpty()) return true;

        CommentApi api = retrofit.create(CommentApi.class);
        Response<CommentDtos.CommentDto> res = api.add(p.taskId, new CommentDtos.AddCommentRequest(p.content)).execute();
        if (!res.isSuccessful() || res.body() == null) return false;

        // Optional: insert comment vào Room nếu bạn track comments offline
        // CommentEntity ce = Mapper.toCommentEntity(res.body());
        // db.commentDao().upsert(ce);
        return true;
    }

    private boolean handleUpdateTaskStatus(String payloadJson) throws Exception {
        // payload: { "taskId":"...", "status":"IN_PROGRESS", "position":1500.0 }
        Type t = new TypeToken<UpdateStatusPayload>(){}.getType();
        UpdateStatusPayload p = gson.fromJson(payloadJson, t);
        if (p == null || p.taskId == null || p.status == null) return true;

        TaskApi api = retrofit.create(TaskApi.class);
        Response<Void> res = api.updateStatus(p.taskId, new TaskDtos.UpdateTaskStatusRequest(p.status, p.position)).execute();
        if (!res.isSuccessful()) return false;

        // Optional: cập nhật local task status nếu muốn phản ánh ngay
        TaskEntity te = db.taskDao().findById(p.taskId);
        if (te != null) {
            te.status = p.status;
            te.position = p.position;
            te.updatedAt = new Date();
            db.taskDao().upsert(te);
        }
        return true;
    }

    private boolean handleSendMessage(String payloadJson) throws Exception {
        // payload: { "conversationId":"...", "body":"..." }
        Type t = new TypeToken<SendMessagePayload>(){}.getType();
        SendMessagePayload p = gson.fromJson(payloadJson, t);
        if (p == null || p.conversationId == null || p.body == null || p.body.trim().isEmpty()) return true;

        MessageApi api = retrofit.create(MessageApi.class);
        Response<MessageDtos.MessageDto> res = api.send(p.conversationId, new MessageDtos.SendMessageRequest(p.body)).execute();
        if (!res.isSuccessful() || res.body() == null) return false;

        MessageEntity me = Mapper.toMessageEntity(res.body());
        db.messageDao().upsert(me);
        return true;
    }

    /* ===================== PULL (delta đơn giản) ===================== */

    private void pullNotifications() throws Exception {
        NotificationApi api = retrofit.create(NotificationApi.class);
        Response<List<NotificationDtos.NotificationDto>> res = api.list().execute();
        if (!res.isSuccessful() || res.body() == null) return;

        List<NotificationEntity> list = Mapper.toNotificationEntities(res.body());
        db.notificationDao().upsertAll(list);
        session.setLastSyncNotifications(new Date());
    }

    private void pullRecentConversationsAndMessages() throws Exception {
        // Lấy các conversation của tôi
        ConversationApi cApi = retrofit.create(ConversationApi.class);
        Response<List<ConversationDtos.ConversationDto>> cRes = cApi.my(1, 50).execute();
        if (!cRes.isSuccessful() || cRes.body() == null) return;

        // Lưu conversations (nếu có entity trong Room)
        // List<ConversationEntity> convEntities = Mapper.toConversationEntities(cRes.body());
        // db.conversationDao().upsertAll(convEntities);

        // Lấy messages mới cho từng conversation (limit nhỏ để tránh tải nặng)
        MessageApi mApi = retrofit.create(MessageApi.class);
        String beforeIso = null; // có thể dùng now hoặc để server trả gần đây
        for (ConversationDtos.ConversationDto c : cRes.body()) {
            Response<List<MessageDtos.MessageDto>> mRes = mApi.list(c.id, beforeIso, 30).execute();
            if (!mRes.isSuccessful() || mRes.body() == null) continue;

            List<MessageEntity> msg = Mapper.toMessageEntities(mRes.body());
            db.messageDao().upsertAll(msg);
        }
        session.setLastSyncChat(new Date());
    }

    /* ===================== Payload models cho pending ===================== */

    private static class AddCommentPayload {
        UUID taskId;
        String content;
    }

    private static class UpdateStatusPayload {
        UUID taskId;
        String status;
        double position;
    }

    private static class SendMessagePayload {
        UUID conversationId;
        String body;
    }

    /* ===================== ISO utils (nếu cần) ===================== */
    @SuppressWarnings("unused")
    private static String toIso(Date d) {
        if (d == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(d);
    }
}
