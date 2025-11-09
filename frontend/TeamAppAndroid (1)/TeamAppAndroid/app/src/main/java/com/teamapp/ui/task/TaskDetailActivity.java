// com/teamapp/ui/task/TaskDetailActivity.java
package com.teamapp.ui.task;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.data.dto.CommentDtos;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.repo.CommentRepository;
import com.teamapp.data.repo.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDesc, tvStatus;
    private Spinner spStatus;           // NEW
    private Button btnSaveStatus;       // NEW
    private TabLayout tab;
    private LinearLayout tabInfo, tabComments;
    private RecyclerView rvComments;
    private EditText edtComment;
    private Button btnSend;
    private ProgressBar progress;

    private UUID taskId;
    private TaskDao taskDao;
    private CommentRepository comments;
    private TaskRepository tasks;       // NEW
    private CommentAdapter adapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvDesc  = findViewById(R.id.tvDesc);
        tvStatus= findViewById(R.id.tvStatus);
        spStatus = findViewById(R.id.spStatus);       // NEW
        btnSaveStatus = findViewById(R.id.btnSaveStatus); // NEW
        tab     = findViewById(R.id.tabLayout);
        tabInfo = findViewById(R.id.tabInfo);
        tabComments = findViewById(R.id.tabComments);
        rvComments = findViewById(R.id.rvComments);
        edtComment = findViewById(R.id.edtComment);
        btnSend    = findViewById(R.id.btnSend);
        progress   = findViewById(R.id.progress);

        taskId = UUID.fromString(getIntent().getStringExtra("taskId"));
        tvTitle.setText(getIntent().getStringExtra("taskTitle"));

        taskDao  = App.get().db().taskDao();
        comments = new CommentRepository(App.get().retrofit(), App.get().db());
        tasks    = new TaskRepository(App.get().retrofit(), App.get().db()); // NEW

        // Status spinner
        ArrayAdapter<String> stAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("ToDo", "InProgress", "Done"));
        spStatus.setAdapter(stAdapter);

        btnSaveStatus.setOnClickListener(v -> saveStatus());

        adapter = new CommentAdapter(new ArrayList<>());
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        tab.addTab(tab.newTab().setText("Info"));
        tab.addTab(tab.newTab().setText("Comments"));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab t) {
                if (t.getPosition() == 0) {
                    tabInfo.setVisibility(View.VISIBLE);
                    tabComments.setVisibility(View.GONE);
                } else {
                    tabInfo.setVisibility(View.GONE);
                    tabComments.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnSend.setOnClickListener(v -> sendComment());

        loadTaskFromRoom();
        loadComments();
    }

    private void loadTaskFromRoom() {
        Executors.newSingleThreadExecutor().execute(() -> {
            TaskEntity e = taskDao.findById(taskId);
            runOnUiThread(() -> {
                if (e != null) {
                    tvDesc.setText(e.description == null ? "" : e.description);
                    String st = e.status == null ? "ToDo" : e.status;
                    tvStatus.setText("Status: " + st);
                    // set spinner selection
                    if ("Done".equalsIgnoreCase(st)) spStatus.setSelection(2);
                    else if ("InProgress".equalsIgnoreCase(st)) spStatus.setSelection(1);
                    else spStatus.setSelection(0);
                } else {
                    tvDesc.setText("(Không tìm thấy task trong bộ nhớ cục bộ)");
                }
            });
        });
    }

    private void loadComments() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<com.teamapp.data.entity.CommentEntity> list = comments.fetchByTask(taskId);
                List<CommentDtos.CommentDto> dtos = new ArrayList<>();
                for (com.teamapp.data.entity.CommentEntity ce : list) {
                    CommentDtos.CommentDto d = new CommentDtos.CommentDto();
                    d.id = ce.id; d.taskId = ce.taskId; d.authorId = ce.authorId;
                    d.content = ce.content; d.createdAt = ce.createdAt;
                    dtos.add(d);
                }
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(dtos);
                });
            } catch (Exception e) {
                runOnUiThread(() -> progress.setVisibility(View.GONE));
            }
        });
    }

    private void sendComment() {
        String text = edtComment.getText().toString().trim();
        if (text.isEmpty()) return;
        edtComment.setText("");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                comments.add(taskId, text);
                loadComments();
            } catch (Exception ignored) {}
        });
    }

    /** NEW: lưu status (không đổi position) */
    private void saveStatus() {
        String status = (String) spStatus.getSelectedItem(); // ToDo | InProgress | Done
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                tasks.updateStatus(taskId, status);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    tvStatus.setText("Status: " + status);
                    Toast.makeText(this, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /* ====== Adapter comments đơn giản ====== */
    private static class CommentAdapter extends RecyclerView.Adapter<CommentVH> {
        private final List<CommentDtos.CommentDto> data;
        CommentAdapter(List<CommentDtos.CommentDto> d){ data=d; }
        void submit(List<CommentDtos.CommentDto> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }

        @Override public CommentVH onCreateViewHolder(android.view.ViewGroup parent, int vt) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new CommentVH(v);
        }
        @Override public void onBindViewHolder(CommentVH h, int pos){ h.bind(data.get(pos)); }
        @Override public int getItemCount(){ return data.size(); }
    }

    private static class CommentVH extends RecyclerView.ViewHolder {
        TextView tvUser, tvText, tvTime;
        private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        CommentVH(View v){ super(v);
            tvUser=v.findViewById(R.id.tvUser);
            tvText=v.findViewById(R.id.tvText);
            tvTime=v.findViewById(R.id.tvTime);
        }
        void bind(CommentDtos.CommentDto c){
            String name = c.authorId != null ? c.authorId.toString().substring(0, 8) : "(user)";
            tvUser.setText(name);
            tvText.setText(c.content);
            tvTime.setText(c.createdAt != null ? fmt.format(c.createdAt) : "");
        }
    }
}
