// app/src/main/java/com/teamapp/ui/project/MembersActivity.java
package com.teamapp.ui.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.dto.MemberDtos;
import com.teamapp.data.repo.ProjectRepository;
import com.teamapp.data.repo.ConversationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class MembersActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProgressBar progress;
    private Button btnInvite, btnOpenKanban, btnOpenChat, btnOpenCalendar;

    private MemberAdapter adapter;
    private ProjectRepository projects;
    private ConversationRepository conversations;

    private UUID projectId;
    private String projectName;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        rv = findViewById(R.id.rvMembers);
        progress = findViewById(R.id.progress);
        btnInvite = findViewById(R.id.btnInvite);
        btnOpenKanban = findViewById(R.id.btnOpenKanban);
        btnOpenChat = findViewById(R.id.btnOpenChat);
        btnOpenCalendar = findViewById(R.id.btnOpenCalendar);

        projects = ServiceLocator.projects();
        conversations = ServiceLocator.conversations();

        projectId = UUID.fromString(getIntent().getStringExtra("projectId"));
        projectName = getIntent().getStringExtra("projectName");
        setTitle("Members • " + (projectName == null ? "" : projectName));

        adapter = new MemberAdapter(new ArrayList<>());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        btnInvite.setOnClickListener(v -> promptInvite());

        btnOpenKanban.setOnClickListener(v -> {
            Intent i = new Intent(this, com.teamapp.ui.task.KanbanActivity.class);
            i.putExtra("projectId", projectId.toString());
            startActivity(i);
        });

        btnOpenCalendar.setOnClickListener(v -> {
            Intent i = new Intent(this, com.teamapp.ui.calendar.CalendarActivity.class);
            i.putExtra("projectId", projectId.toString());
            startActivity(i);
        });

        btnOpenChat.setOnClickListener(v -> openProjectChat());

        loadMembers();
    }

    private void loadMembers() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<MemberDtos.MemberDto> list = projects.members(projectId);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(list);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Không tải được thành viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void promptInvite() {
        final EditText input = new EditText(this);
        input.setHint("Email người dùng");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Mời thành viên")
                .setView(input)
                .setPositiveButton("Gửi", (d, w) -> inviteByEmail(input.getText().toString().trim()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void inviteByEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                projects.inviteByEmail(projectId, email);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Snackbar.make(rv, "Đã mời " + email, Snackbar.LENGTH_LONG)
                            .setAction("Mở Kanban", v2 -> {
                                Intent i = new Intent(this, com.teamapp.ui.task.KanbanActivity.class);
                                i.putExtra("projectId", projectId.toString());
                                startActivity(i);
                            })
                            .show();
                    loadMembers();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Mời thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /** Tạo group chat dự án (bắt buộc memberIds) rồi mở ChatActivity */
    private void openProjectChat() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // 1) Lấy danh sách thành viên
                List<MemberDtos.MemberDto> mList = projects.members(projectId);

                // 2) Map -> memberIds (UUID userId)
                ArrayList<UUID> memberIds = new ArrayList<>();
                if (mList != null) {
                    for (MemberDtos.MemberDto m : mList) {
                        if (m.userId != null) memberIds.add(m.userId);
                    }
                }
                if (memberIds.isEmpty()) {
                    throw new IllegalStateException("Không có thành viên nào trong dự án để tạo nhóm chat.");
                }

                // 3) Tạo group chat
                UUID conversationId = conversations.createProjectGroupAndReturnId(
                        projectId,
                        (projectName != null ? projectName : "Project Chat"),
                        memberIds
                );

                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Intent i = new Intent(this, com.teamapp.ui.chat.ChatActivity.class);
                    i.putExtra("conversationId", conversationId.toString());
                    i.putExtra("conversationTitle", projectName);
                    startActivity(i);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Không mở được chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /* ==== Adapter ==== */
    private static class MemberAdapter extends RecyclerView.Adapter<MemberVH> {
        private final List<MemberDtos.MemberDto> data;
        MemberAdapter(List<MemberDtos.MemberDto> data){ this.data=data; }
        void submit(List<MemberDtos.MemberDto> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }

        @Override public MemberVH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_member, parent, false);
            return new MemberVH(v);
        }
        @Override public void onBindViewHolder(MemberVH h, int pos) { h.bind(data.get(pos)); }
        @Override public int getItemCount() { return data.size(); }
    }
    private static class MemberVH extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        MemberVH(View v){ super(v);
            tvName = v.findViewById(R.id.tvName);
            tvEmail = v.findViewById(R.id.tvEmail);
            tvRole = v.findViewById(R.id.tvRole);
        }
        void bind(MemberDtos.MemberDto m){
            tvName.setText(m.fullName);
            tvEmail.setText(m.email);
            tvRole.setText(m.role);
        }
    }
}
