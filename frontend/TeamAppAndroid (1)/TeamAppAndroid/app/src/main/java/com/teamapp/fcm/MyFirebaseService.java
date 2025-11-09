package com.teamapp.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.api.DeviceApi;
import com.teamapp.data.dto.DeviceDtos;
import com.teamapp.ui.chat.ChatActivity;
import com.teamapp.ui.main.MainActivity;
import com.teamapp.ui.notif.NotificationsActivity;
import com.teamapp.ui.task.TaskDetailActivity;

import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Nhận push từ FCM và hiển thị Notification + deep-link.
 * Data payload hỗ trợ (ví dụ):
 *  - type=TASK_ASSIGNED | TASK_COMMENTED | TASK_STATUS_CHANGED + taskId
 *  - type=CHAT_MESSAGE + conversationId + conversationTitle + message
 *  - type=GENERIC (fallback)
 */
public class MyFirebaseService extends FirebaseMessagingService {

    // ---- Notification Channel ----
    public static final String CHAN_ID_DEFAULT = "teamapp.default";
    public static final String CHAN_NAME_DEFAULT = "TeamApp Notifications";

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(
                    CHAN_ID_DEFAULT, CHAN_NAME_DEFAULT, NotificationManager.IMPORTANCE_HIGH);
            chan.enableLights(true);
            chan.setLightColor(Color.CYAN);
            chan.enableVibration(true);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(chan);
        }
    }

    // ---- FCM token ----
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Lưu tạm trong prefs (nếu cần)
        App.get().session().setFcmToken(token);
        // Gửi lên server: /api/devices  (token gắn với user hiện tại)
        syncTokenToBackend(token);
    }

    private void syncTokenToBackend(String token) {
        // Chỉ gửi khi đã có phiên đăng nhập (JWT)
        SessionStore session = App.get().session();
        if (session.getToken() == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                DeviceApi api = App.get().retrofit().create(DeviceApi.class);
                Response<Void> res = api.save(new DeviceDtos.SaveDeviceTokenRequest(token, "android")).execute();
                // Không cần xử lý UI — đây là service nền
            } catch (Exception ignored) {}
        });
    }

    // ---- Nhận message ----
    @Override
    public void onMessageReceived(@NonNull RemoteMessage msg) {
        super.onMessageReceived(msg);
        ensureChannel();

        // Ưu tiên data payload để deep-link; nếu không có thì dùng notification payload.
        Map<String, String> data = msg.getData();
        String title = null;
        String body = null;

        if (msg.getNotification() != null) {
            // Notification payload (từ console hoặc legacy)
            title = msg.getNotification().getTitle();
            body  = msg.getNotification().getBody();
        }
        if (data != null && !data.isEmpty()) {
            // data có thể override title/body
            if (TextUtils.isEmpty(title)) title = data.get("title");
            if (TextUtils.isEmpty(body))  body  = data.get("body");
            showDeepLinkNotification(data, title, body);
        } else {
            // Không có data => mở app chính
            showGenericNotification(title, body);
        }
    }

    // ---- Notification builders ----
    private void showDeepLinkNotification(Map<String, String> data, String title, String body) {
        String type = safe(data.get("type"));
        PendingIntent pi = buildPendingIntentForType(type, data);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHAN_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification_24)
                .setContentTitle(defaultIfEmpty(title, getString(R.string.app_name)))
                .setContentText(defaultIfEmpty(body, ""))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(defaultIfEmpty(body, "")))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), b.build());
    }

    private void showGenericNotification(String title, String body) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pi = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
                .addNextIntent(intent)
                .getPendingIntent((int) System.currentTimeMillis(),
                        Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHAN_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification_24)
                .setContentTitle(defaultIfEmpty(title, getString(R.string.app_name)))
                .setContentText(defaultIfEmpty(body, ""))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(defaultIfEmpty(body, "")))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), b.build());
    }

    // ---- Deep-link routing ----
    private PendingIntent buildPendingIntentForType(String type, Map<String, String> data) {
        Intent target;

        try {
            switch (type) {
                case "TASK_ASSIGNED":
                case "TASK_COMMENTED":
                case "TASK_STATUS_CHANGED": {
                    String taskIdStr = data.get("taskId");
                    if (!TextUtils.isEmpty(taskIdStr)) {
                        target = new Intent(this, TaskDetailActivity.class);
                        target.putExtra("taskId", taskIdStr);
                        target.putExtra("taskTitle", defaultIfEmpty(data.get("taskTitle"), "Task"));
                        return stackIntent(target);
                    }
                    break;
                }
                case "CHAT_MESSAGE": {
                    String convIdStr = data.get("conversationId");
                    String convTitle = defaultIfEmpty(data.get("conversationTitle"), "Chat");
                    if (!TextUtils.isEmpty(convIdStr)) {
                        target = new Intent(this, ChatActivity.class);
                        target.putExtra("conversationId", convIdStr);
                        target.putExtra("conversationTitle", convTitle);
                        return stackIntent(target);
                    }
                    break;
                }
                default:
                    // GENERIC / JOIN_REQUEST / JOIN_APPROVED … tùy backend:
                    break;
            }
        } catch (Exception ignored) {
            // UUID parse hoặc key thiếu -> fallback
        }

        // Fallback: mở màn Notifications
        target = new Intent(this, NotificationsActivity.class);
        return stackIntent(target);
    }

    private PendingIntent stackIntent(Intent target) {
        return TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(new Intent(this, MainActivity.class))
                .addNextIntent(target)
                .getPendingIntent(
                        (int) System.currentTimeMillis(),
                        Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT
                );
    }

    // ---- Helpers ----
    private String safe(String s) { return s == null ? "" : s; }
    private String defaultIfEmpty(String s, String d) { return TextUtils.isEmpty(s) ? d : s; }

    // Public helper để chủ động đăng ký/refresh token sau đăng nhập
    public static void refreshAndSyncTokenIfNeeded(Context ctx) {
        // Lấy token đã có (từ FirebaseMessaging hoặc SessionStore) và sync
        String token = App.get().session().getFcmToken();
        if (!TextUtils.isEmpty(token)) {
            // Gọi service để sync ngay
            MyFirebaseService svc = new MyFirebaseService();
            // không thể khởi tạo service theo cách thường — nên gọi API trực tiếp:
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    DeviceApi api = App.get().retrofit().create(DeviceApi.class);
                    api.save(new DeviceDtos.SaveDeviceTokenRequest(token, "android")).execute();
                } catch (Exception ignored) {}
            });
        }
    }
}
