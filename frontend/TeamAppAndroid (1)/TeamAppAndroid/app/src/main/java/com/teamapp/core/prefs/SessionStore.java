package com.teamapp.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.teamapp.core.util.JwtUtils; // (*) added

import java.util.Date;
import java.util.UUID;

/**
 * Lưu phiên đăng nhập (JWT, user) + các mốc đồng bộ (lastSync) cho từng module.
 * Ưu tiên dùng EncryptedSharedPreferences; fallback sang SharedPreferences thường nếu thiết bị không hỗ trợ.
 */
public class SessionStore {

    private static final String PREF_NAME = "session_prefs";

    // Auth & user
    private static final String KEY_JWT = "jwt";
    private static final String KEY_REFRESH = "refresh_token"; // (*) added
    private static final String KEY_USER_JSON = "user_json";
    private static final String KEY_USER_ID = "user_id";

    // FCM
    private static final String KEY_FCM_TOKEN = "fcm_token";

    // Các mốc lastSync theo module (để pull delta nếu có)
    private static final String KEY_LAST_SYNC_NOTIF = "last_sync_notifications";
    private static final String KEY_LAST_SYNC_CHAT   = "last_sync_chat";
    private static final String KEY_LAST_SYNC_TASKS  = "last_sync_tasks";

    private final SharedPreferences prefs;

    public SessionStore(Context ctx) {
        SharedPreferences p;
        try {
            MasterKey key = new MasterKey.Builder(ctx)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            p = EncryptedSharedPreferences.create(
                    ctx,
                    PREF_NAME,
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Throwable e) {
            // Fallback nếu thiết bị quá cũ
            p = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = p;
    }

    /* ================== Token & User ================== */

    /** Lưu JWT (access token) — luôn lưu token THÔ, không kèm "Bearer "  */
    public void saveToken(@Nullable String token) { // (*) changed
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }
        prefs.edit().putString(KEY_JWT, token).apply();
    }

    /** Đọc JWT */
    @Nullable
    public String getToken() {
        return prefs.getString(KEY_JWT, null);
    }

    /** (Tuỳ chọn) Lưu refresh token để sau này auto refresh 401 */
    public void saveRefreshToken(@Nullable String refresh) { // (*) added
        prefs.edit().putString(KEY_REFRESH, refresh).apply();
    }

    /** (Tuỳ chọn) Đọc refresh token */
    @Nullable
    public String getRefreshToken() { // (*) added
        return prefs.getString(KEY_REFRESH, null);
    }

    /** Lưu JSON thông tin user (serialize từ đối tượng User) */
    public void saveUserJson(@Nullable String json) {
        prefs.edit().putString(KEY_USER_JSON, json).apply();
    }

    /** Đọc JSON thông tin user */
    @Nullable
    public String getUserJson() {
        return prefs.getString(KEY_USER_JSON, null);
    }

    /** Lưu UUID user (nếu có) */
    public void saveUserId(@Nullable UUID id) {
        prefs.edit().putString(KEY_USER_ID, id != null ? id.toString() : null).apply();
    }

    /** Đọc UUID user (có thể null nếu chưa lưu hoặc chuỗi không hợp lệ) */
    @Nullable
    public UUID getUserId() {
        String s = prefs.getString(KEY_USER_ID, null);
        try {
            return s == null ? null : UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    /** Xoá toàn bộ dữ liệu phiên (đăng xuất) */
    public void clear() {
        prefs.edit().clear().apply();
    }

    /* ================== FCM Token ================== */

    /** Lưu FCM device token để sync lên backend */
    public void setFcmToken(@Nullable String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    /** Đọc FCM device token hiện tại */
    @Nullable
    public String getFcmToken() {
        return prefs.getString(KEY_FCM_TOKEN, null);
    }

    /* ================== Last Sync helpers ================== */

    public void setLastSyncNotifications(@Nullable Date d) {
        prefs.edit().putLong(KEY_LAST_SYNC_NOTIF, d != null ? d.getTime() : 0L).apply();
    }

    @Nullable
    public Date getLastSyncNotifications() {
        long v = prefs.getLong(KEY_LAST_SYNC_NOTIF, 0L);
        return v == 0L ? null : new Date(v);
    }

    public void setLastSyncChat(@Nullable Date d) {
        prefs.edit().putLong(KEY_LAST_SYNC_CHAT, d != null ? d.getTime() : 0L).apply();
    }

    @Nullable
    public Date getLastSyncChat() {
        long v = prefs.getLong(KEY_LAST_SYNC_CHAT, 0L);
        return v == 0L ? null : new Date(v);
    }

    public void setLastSyncTasks(@Nullable Date d) {
        prefs.edit().putLong(KEY_LAST_SYNC_TASKS, d != null ? d.getTime() : 0L).apply();
    }

    @Nullable
    public Date getLastSyncTasks() {
        long v = prefs.getLong(KEY_LAST_SYNC_TASKS, 0L);
        return v == 0L ? null : new Date(v);
    }

    /* ================== Tiện ích bổ sung ================== */

    /** Có token không (chỉ check null/empty) */
    public boolean hasToken() {
        String t = getToken();
        return t != null && !t.isEmpty();
    }

    /** Token còn hạn? Dùng exp trong JWT, cộng buffer 30s để an toàn. */
    public boolean hasValidToken() { // (*) added
        String t = getToken();
        if (t == null || t.isEmpty()) return false;
        return !JwtUtils.isExpired(t, 30);
    }
}