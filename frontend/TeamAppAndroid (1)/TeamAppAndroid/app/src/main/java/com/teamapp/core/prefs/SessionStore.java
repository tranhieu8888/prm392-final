package com.teamapp.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.teamapp.App;
import com.teamapp.core.util.JwtUtils;
import com.teamapp.data.dto.ProfileDtos;

import java.util.Date;
import java.util.UUID;

public class SessionStore {

    private static final String PREF_NAME = "session_prefs";

    private static final String KEY_JWT = "jwt";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_USER_JSON = "user_json";
    private static final String KEY_USER_ID = "user_id";

    private static final String KEY_FCM_TOKEN = "fcm_token";

    private static final String KEY_LAST_SYNC_NOTIF = "last_sync_notifications";
    private static final String KEY_LAST_SYNC_CHAT   = "last_sync_chat";
    private static final String KEY_LAST_SYNC_TASKS  = "last_sync_tasks";

    private final SharedPreferences prefs;
    private final Gson gson;

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
            p = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = p;
        this.gson = App.get().gson();
    }

    /* ================== Token & User ================== */

    public void saveToken(@Nullable String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring("Bearer ".length());
        }
        prefs.edit().putString(KEY_JWT, token).apply();
    }

    @Nullable
    public String getToken() {
        return prefs.getString(KEY_JWT, null);
    }

    public void saveRefreshToken(@Nullable String refresh) {
        prefs.edit().putString(KEY_REFRESH, refresh).apply();
    }

    @Nullable
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }

    public void saveUser(@Nullable ProfileDtos.UserDto user) {
        if (user != null) {
            saveUserJson(gson.toJson(user));
        } else {
            saveUserJson(null);
        }
    }

    @Nullable
    public ProfileDtos.UserDto getUser() {
        String json = getUserJson();
        if (json != null) {
            try {
                return gson.fromJson(json, ProfileDtos.UserDto.class);
            } catch (Exception e) {
                // Log or handle deserialization error
                return null;
            }
        }
        return null;
    }

    public void saveUserJson(@Nullable String json) {
        prefs.edit().putString(KEY_USER_JSON, json).apply();
    }

    @Nullable
    public String getUserJson() {
        return prefs.getString(KEY_USER_JSON, null);
    }

    public void saveUserId(@Nullable UUID id) {
        prefs.edit().putString(KEY_USER_ID, id != null ? id.toString() : null).apply();
    }

    @Nullable
    public UUID getUserId() {
        String s = prefs.getString(KEY_USER_ID, null);
        try {
            return s == null ? null : UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    /* ================== FCM Token ================== */

    public void setFcmToken(@Nullable String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

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

    public boolean hasToken() {
        String t = getToken();
        return t != null && !t.isEmpty();
    }

    public boolean hasValidToken() {
        String t = getToken();
        if (t == null || t.isEmpty()) return false;
        return !JwtUtils.isExpired(t, 30);
    }
}
