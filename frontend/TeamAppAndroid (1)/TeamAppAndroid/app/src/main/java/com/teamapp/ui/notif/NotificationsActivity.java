// app/src/main/java/com/teamapp/ui/notif/NotificationsActivity.java
package com.teamapp.ui.notif;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.entity.NotificationEntity;
import com.teamapp.data.repo.NotificationRepository;
import com.teamapp.ui.widgets.NotificationAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar progress;
    private ImageButton btnRefresh, btnMarkAll;
    private NotificationAdapter adapter;
    private NotificationRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rv = findViewById(R.id.rvNotifications);
        progress = findViewById(R.id.progress);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnMarkAll = findViewById(R.id.btnMarkAll);

        repo = new NotificationRepository(App.get().retrofit(), App.get().db());

        adapter = new NotificationAdapter(new NotificationAdapter.OnItemClick() {
            @Override public void onClick(NotificationEntity n) {
                // tuỳ ý: mở chi tiết, deep-link theo n.type/dataJson
            }

            @Override public void onMarkClick(NotificationEntity n) {
                // toggle read -> read
                if (!n.isRead) markOne(n);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnRefresh.setOnClickListener(v -> syncFromServer());
        btnMarkAll.setOnClickListener(v -> markAll());

        // 1) hiển thị cache ngay
        loadLocal();
        // 2) đồng bộ server
        syncFromServer();
    }

    private void loadLocal() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<NotificationEntity> local = repo.recentLocal();
            runOnUiThread(() -> adapter.submit(local));
        });
    }

    private void syncFromServer() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<NotificationEntity> list = repo.fetchAllSafe();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(list);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void markOne(NotificationEntity n) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                repo.mark(n.id, true);
                runOnUiThread(() -> adapter.markReadLocal(n.id));
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Không đánh dấu được: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void markAll() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                repo.markAllRead();
                // Sau khi mark-all, reload cache
                List<NotificationEntity> list = repo.recentLocal();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(list);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
