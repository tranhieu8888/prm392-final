// app/src/main/java/com/teamapp/ui/chat/ChatActivity.java
package com.teamapp.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.realtime.SignalRClient;
import com.teamapp.data.api.MessageApi;
import com.teamapp.data.dto.MessageDtos;
import com.teamapp.ui.widgets.MessageAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EditText edtMessage;
    private ImageButton btnSend;
    private TextView tvTitle;
    private ProgressBar progress;

    private MessageAdapter adapter;
    private MessageApi messageApi;
    @Nullable private SignalRClient signalr;
    private UUID conversationId;

    private final SimpleDateFormat isoParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        isoParser.setTimeZone(TimeZone.getTimeZone("UTC"));

        rv         = findViewById(R.id.rvMessages);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend    = findViewById(R.id.btnSend);
        tvTitle    = findViewById(R.id.tvTitle);
        progress   = findViewById(R.id.progress);

        String rawId = getIntent().getStringExtra("conversationId");
        String title = getIntent().getStringExtra("conversationTitle");
        tvTitle.setText(title != null ? title : "Chat");

        if (rawId == null) {
            Toast.makeText(this, "Thiếu conversationId", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        try { conversationId = UUID.fromString(rawId); }
        catch (IllegalArgumentException ex) {
            Toast.makeText(this, "conversationId không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        adapter = new MessageAdapter(new ArrayList<>());
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        rv.setHasFixedSize(false);

        messageApi = App.get().retrofit().create(MessageApi.class);
        signalr    = App.get().signalR();

        if (signalr != null) {
            signalr.join(conversationId);
            signalr.setOnMessageListener(env -> {
                if (env == null || env.conversationId == null || env.message == null) return;
                if (!env.conversationId.equals(conversationId)) return;

                MessageDtos.MessageDto m = new MessageDtos.MessageDto();
                m.id = env.message.id != null ? env.message.id : UUID.randomUUID();
                m.conversationId = env.message.conversationId != null ? env.message.conversationId : conversationId;
                m.senderId = env.message.senderId;
                m.body = env.message.body;
                try { if (env.message.createdAt != null) m.createdAt = isoParser.parse(env.message.createdAt); }
                catch (ParseException ignored) {}

                runOnUiThread(() -> {
                    adapter.add(m);
                    rv.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));
                });
            });
        }

        btnSend.setOnClickListener(v -> sendMessage());
        loadMessages();
    }

    private void loadMessages() {
        progress.setVisibility(ProgressBar.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<MessageDtos.MessageDto>> res =
                        messageApi.list(conversationId, null, 50).execute();

                final int code = res.code();
                final List<MessageDtos.MessageDto> body =
                        (res.isSuccessful() && res.body() != null) ? res.body() : new ArrayList<>();

                runOnUiThread(() -> {
                    progress.setVisibility(ProgressBar.GONE);
                    adapter.submit(body);
                    if (body.isEmpty()) {
                        Toast.makeText(this, "Chưa có tin nhắn.", Toast.LENGTH_SHORT).show();
                    }
                    rv.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Lỗi tải tin nhắn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendMessage() {
        String text = edtMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        edtMessage.setText("");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                messageApi.send(conversationId, new MessageDtos.SendMessageRequest(text)).execute();
            } catch (Exception ignored) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (signalr != null) signalr.leave(conversationId);
    }
}
