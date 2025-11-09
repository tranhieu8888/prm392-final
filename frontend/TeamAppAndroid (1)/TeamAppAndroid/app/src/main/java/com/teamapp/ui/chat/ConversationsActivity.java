package com.teamapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.api.ConversationApi;
import com.teamapp.data.dto.ConversationDtos;
import com.teamapp.ui.widgets.ConversationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

/** Danh sách các cuộc trò chuyện */
public class ConversationsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar progress;
    private FloatingActionButton fab;
    private ConversationApi conversationApi;
    private ConversationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        rv = findViewById(R.id.rvConversations);
        progress = findViewById(R.id.progress);
        fab = findViewById(R.id.fabAdd);

        conversationApi = App.get().retrofit().create(ConversationApi.class);
        adapter = new ConversationAdapter(new ArrayList<>(), this::openChat);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng tạo group đang phát triển", Toast.LENGTH_SHORT).show()
        );

        loadData();
    }

    private void loadData() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<ConversationDtos.ConversationDto>> res =
                        conversationApi.my(1, 50).execute();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    if (res.isSuccessful() && res.body() != null) {
                        adapter.submit(res.body());
                    } else {
                        Toast.makeText(this, "Không tải được danh sách", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void openChat(ConversationDtos.ConversationDto c) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("conversationId", c.id.toString());
        i.putExtra("conversationTitle", c.title);
        startActivity(i);
    }
}
