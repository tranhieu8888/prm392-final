package com.teamapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamapp.core.db.AppDb;
import com.teamapp.core.network.ApiClient;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.core.realtime.SignalRClient;
import com.teamapp.core.worker.SyncWorker;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;

/**
 * Application entrypoint – khởi tạo hạ tầng: SessionStore, Room, Retrofit, WorkManager, SignalR.
 *
 * Đừng quên khai báo trong AndroidManifest:
 * <application
 *     android:name=".App"
 *     ...>
 * </application>
 */
public class App extends Application {

    public static final String NOTIF_CHANNEL_GENERAL = "teamapp_general";
    public static final String NOTIF_CHANNEL_MESSAGES = "teamapp_messages";
    private static final String SYNC_WORK_NAME = "teamapp_periodic_sync";

    private static App instance;

    // Core singletons
    private SessionStore sessionStore;
    private AppDb db;
    private Retrofit retrofit;
    private Gson gson;

    // Realtime (tuỳ chọn)
    private SignalRClient signalR;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 1) Gson chuẩn ISO-8601 (khớp backend)
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create();

        // 2) Session (EncryptedSharedPreferences)
        sessionStore = new SessionStore(this);

        // 3) Room Database
        db = AppDb.create(this);

        // 4) Retrofit client (OkHttp + AuthInterceptor đã gắn JWT)
        retrofit = ApiClient.get(sessionStore);

        // 5) Notification Channels (cho FCM)
        createNotificationChannels();

        // 6) WorkManager: Periodic Sync (đồng bộ offline mỗi 15 phút khi có mạng)
        schedulePeriodicSync();

        // 7) SignalR client (không tự kết nối nếu chưa login)
        signalR = new SignalRClient();
        autoConnectSignalRIfLoggedIn();
    }

    /* =========================
       Public getters (Service Locator đơn giản)
       ========================= */
    @NonNull public static App get() { return instance; }
    @NonNull public SessionStore session() { return sessionStore; }
    @NonNull public AppDb db() { return db; }
    @NonNull public Retrofit retrofit() { return retrofit; }
    @NonNull public Gson gson() { return gson; }
    @NonNull public SignalRClient signalR() { return signalR; }

    /* =========================
       Notification Channels (Android 8+)
       ========================= */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel general = new NotificationChannel(
                NOTIF_CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        general.setDescription("Thông báo chung của TeamApp");

        NotificationChannel messages = new NotificationChannel(
                NOTIF_CHANNEL_MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
        );
        messages.setDescription("Tin nhắn & cuộc hội thoại");

        nm.createNotificationChannel(general);
        nm.createNotificationChannel(messages);
    }

    /* =========================
       WorkManager – Periodic Sync
       ========================= */
    private void schedulePeriodicSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                15, TimeUnit.MINUTES // khoảng tối thiểu của WorkManager
        )
                .setConstraints(constraints)
                .addTag(SYNC_WORK_NAME)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                req
        );
    }

    /* =========================
       SignalR helpers
       ========================= */
    private void autoConnectSignalRIfLoggedIn() {
        String jwt = sessionStore.getToken();
        if (jwt != null && !jwt.isEmpty()) {
            connectSignalR(jwt);
        }
    }

    /**
     * Gọi sau khi đăng nhập thành công (có JWT).
     * Ví dụ trong LoginActivity: App.get().connectSignalR(authResponse.token)
     */
    public void connectSignalR(@NonNull String jwt) {
        // baseUrl của ApiClient (giống Retrofit) – nhớ không có trailing slash khi cộng /hubs/chat bên trong client
        String baseUrl = ApiClient.get(sessionStore)
                .baseUrl()
                .toString()
                .replaceAll("/$", ""); // bỏ dấu / cuối
        signalR.start(baseUrl, jwt, messageDto -> {
            // Nhận message realtime: insert vào Room
            // ví dụ:
            // App.get().db().messageDao().upsert(Mapper.toMessageEntity(messageDto));
        });
    }

    /**
     * Gọi khi logout hoặc khi Activity/Flow chat cần dừng realtime.
     */
    public void disconnectSignalR() {
        if (signalR != null) {
            signalR.stop();
        }
    }
}
