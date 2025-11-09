package com.teamapp.data.repo;

import android.util.Log;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.ConversationApi;
import com.teamapp.data.api.MessageApi;
import com.teamapp.data.dto.ConversationDtos;
import com.teamapp.data.dto.MessageDtos;
import com.teamapp.data.entity.ConversationEntity;
import com.teamapp.data.entity.MessageEntity;
import com.teamapp.data.mapper.Mapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConversationRepository {
    private static final String TAG = "ConversationRepo";

    private final ConversationApi convApi;
    private final MessageApi msgApi;
    private final AppDb db;

    public ConversationRepository(Retrofit retrofit, AppDb db) {
        this.convApi = retrofit.create(ConversationApi.class);
        this.msgApi  = retrofit.create(MessageApi.class);
        this.db = db;
    }

    /* ---------------- helpers ---------------- */

    private static String readBodySafe(ResponseBody body) {
        try { return body != null ? body.string() : null; }
        catch (Exception ignore) { return null; }
    }

    private static String readErrorBodySafe(Response<?> res) {
        try { return res.errorBody() != null ? res.errorBody().string() : ""; }
        catch (Exception ignore) { return ""; }
    }

    private static String buildHttpError(String action, String path, Response<?> res) {
        String body = readErrorBodySafe(res);
        return action + " thất bại (" + path + ") - HTTP " + res.code()
                + (body.isEmpty() ? "" : " | body: " + body);
    }

    private static IOException httpException(String action, String path, Response<?> res) {
        return new IOException(buildHttpError(action, path, res));
    }

    /** Parse body có thể là:
     *  1) "uuid-string"
     *  2) { "conversationId": "uuid-string" }
     *  3) uuid-string trần không ngoặc
     */
    private static UUID parseConversationId(String raw) throws Exception {
        if (raw == null) throw new IllegalStateException("Empty response");
        String s = raw.trim();
        if (s.isEmpty()) throw new IllegalStateException("Empty response");

        // "uuid"
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1);
            return UUID.fromString(s);
        }

        // { "conversationId": "..." }
        if (s.startsWith("{")) {
            JSONObject obj = new JSONObject(s);
            String id = obj.optString("conversationId", null);
            if (id == null || id.isEmpty())
                throw new IllegalStateException("conversationId missing in JSON");
            return UUID.fromString(id);
        }

        // uuid trần
        return UUID.fromString(s);
    }

    /* ---------------- APIs ---------------- */

    /** GET /api/conversations/my */
    public List<ConversationEntity> fetchMy(int page, int size) throws Exception {
        final String path = "/api/conversations/my?page=" + page + "&pageSize=" + size;
        Response<List<ConversationDtos.ConversationDto>> res = convApi.my(page, size).execute();
        if (!res.isSuccessful() || res.body() == null) {
            Log.w(TAG, buildHttpError("Tải hội thoại", path, res));
            throw httpException("Tải hội thoại", path, res);
        }
        List<ConversationEntity> list = Mapper.toConversationEntities(res.body());
        if (db.conversationDao() != null) db.conversationDao().upsertAll(list);
        return list;
    }

    /** GET /api/conversations/{id}/messages */
    public List<MessageEntity> fetchMessages(UUID convId, String beforeIso, Integer size) throws Exception {
        String qs = (beforeIso != null ? "&before=" + beforeIso : "")
                + (size != null ? "&pageSize=" + size : "");
        final String path = "/api/conversations/" + convId + "/messages"
                + (qs.isEmpty() ? "" : "?" + qs.substring(1));

        Response<List<MessageDtos.MessageDto>> res = msgApi.list(convId, beforeIso, size).execute();
        if (!res.isSuccessful() || res.body() == null) {
            Log.w(TAG, buildHttpError("Tải tin nhắn", path, res));
            throw httpException("Tải tin nhắn", path, res);
        }
        List<MessageEntity> list = Mapper.toMessageEntities(res.body());
        db.messageDao().upsertAll(list);
        return list;
    }

    /** POST /api/conversations/{id}/messages */
    public MessageEntity sendMessage(UUID convId, String text) throws Exception {
        final String path = "/api/conversations/" + convId + "/messages";
        Response<MessageDtos.MessageDto> res =
                msgApi.send(convId, new MessageDtos.SendMessageRequest(text)).execute();
        if (!res.isSuccessful() || res.body() == null) {
            Log.w(TAG, buildHttpError("Gửi tin nhắn", path, res));
            throw httpException("Gửi tin nhắn", path, res);
        }
        MessageEntity e = Mapper.toMessageEntity(res.body());
        db.messageDao().upsert(e);
        return e;
    }

    /** POST /api/conversations/group → backend có thể trả "uuid-string" */
    public UUID createProjectGroupAndReturnId(UUID projectId, String title, List<UUID> memberIds) throws Exception {
        final String path = "/api/conversations/group";

        ConversationDtos.CreateGroupRequest body =
                new ConversationDtos.CreateGroupRequest(projectId, title, memberIds);

        Response<ResponseBody> res = convApi.group(body).execute();
        String raw = readBodySafe(res.body());
        if (!res.isSuccessful()) {
            if (raw == null) raw = readErrorBodySafe(res);
            Log.w(TAG, "createProjectGroupAndReturnId() error: " + raw);
            throw httpException("Tạo group chat", path, res);
        }
        UUID id = parseConversationId(raw);
        Log.d(TAG, "createProjectGroupAndReturnId() ok: " + id);
        return id;
    }

    /** POST /api/conversations/dm → parse kiểu trả về tương tự group */
    public UUID startDmAndReturnId(UUID otherUserId) throws Exception {
        final String path = "/api/conversations/dm";
        ConversationDtos.StartDmRequest body = new ConversationDtos.StartDmRequest(otherUserId);

        Response<ResponseBody> res = convApi.dm(body).execute();
        String raw = readBodySafe(res.body());
        if (!res.isSuccessful()) {
            if (raw == null) raw = readErrorBodySafe(res);
            Log.w(TAG, "startDmAndReturnId() error: " + raw);
            throw httpException("Tạo DM", path, res);
        }
        UUID id = parseConversationId(raw);
        Log.d(TAG, "startDmAndReturnId() ok: " + id);
        return id;
    }

    /** Helper: alias ensure (cần memberIds theo yêu cầu backend) */
    public UUID ensureProjectConversation(UUID projectId, String title, List<UUID> memberIds) throws Exception {
        return createProjectGroupAndReturnId(projectId, title, memberIds);
    }

    /** Overload cũ: báo lỗi rõ để caller sửa (giữ để tránh gọi nhầm) */
    @Deprecated
    public UUID createProjectGroupAndReturnId(UUID projectId, String title) throws Exception {
        throw new IOException("API yêu cầu MemberIds bắt buộc. Hãy dùng createProjectGroupAndReturnId(projectId, title, memberIds).");
    }
}
