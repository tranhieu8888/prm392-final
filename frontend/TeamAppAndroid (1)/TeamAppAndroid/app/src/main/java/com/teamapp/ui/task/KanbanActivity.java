// com/teamapp/ui/task/KanbanActivity.java (cập nhật)
package com.teamapp.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
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
import com.teamapp.data.dto.TaskDtos.TaskDto;
import com.teamapp.data.repo.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class KanbanActivity extends AppCompatActivity {

    private RecyclerView rvTodo, rvDoing, rvDone;
    private ProgressBar progress;
    private FloatingActionButton fab;

    private UUID projectId;
    private TaskRepository tasks;

    private TaskCardAdapter adTodo, adDoing, adDone;

    // Lưu task chọn để đổi trạng thái qua context menu
    private TaskDto selectedTask;

    private static final int MENU_TODO = 1;
    private static final int MENU_DOING = 2;
    private static final int MENU_DONE = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanban);

        rvTodo = findViewById(R.id.rvTodo);
        rvDoing = findViewById(R.id.rvDoing);
        rvDone = findViewById(R.id.rvDone);
        progress = findViewById(R.id.progress);
        fab = findViewById(R.id.fabAdd);

        projectId = UUID.fromString(getIntent().getStringExtra("projectId"));
        tasks = new TaskRepository(App.get().retrofit(), App.get().db());

        adTodo  = new TaskCardAdapter(new ArrayList<>(), this::openDetail, this::openMenu);
        adDoing = new TaskCardAdapter(new ArrayList<>(), this::openDetail, this::openMenu);
        adDone  = new TaskCardAdapter(new ArrayList<>(), this::openDetail, this::openMenu);

        rvTodo.setLayoutManager(new LinearLayoutManager(this));
        rvDoing.setLayoutManager(new LinearLayoutManager(this));
        rvDone.setLayoutManager(new LinearLayoutManager(this));

        rvTodo.setAdapter(adTodo);
        rvDoing.setAdapter(adDoing);
        rvDone.setAdapter(adDone);

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateTaskActivity.class);
            i.putExtra("projectId", projectId.toString());
            startActivity(i);
        });

        loadTasks();
    }

    @Override protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<com.teamapp.data.entity.TaskEntity> list = tasks.fetchByProject(projectId);
                List<TaskDto> todo = new ArrayList<>();
                List<TaskDto> doing = new ArrayList<>();
                List<TaskDto> done = new ArrayList<>();
                for (com.teamapp.data.entity.TaskEntity e : list) {
                    TaskDto d = new TaskDto();
                    d.id = e.id; d.projectId = e.projectId; d.title = e.title;
                    d.description = e.description; d.status = e.status; d.position = e.position;
                    d.dueDate = e.dueDate; d.updatedAt = e.updatedAt;

                    String s = (d.status == null) ? "ToDo" : d.status;
                    if ("InProgress".equalsIgnoreCase(s)) doing.add(d);
                    else if ("Done".equalsIgnoreCase(s)) done.add(d);
                    else todo.add(d);
                }
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adTodo.submit(todo);
                    adDoing.submit(doing);
                    adDone.submit(done);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Không tải được task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void openDetail(TaskDto t) {
        Intent i = new Intent(this, TaskDetailActivity.class);
        i.putExtra("taskId", t.id.toString());
        i.putExtra("taskTitle", t.title);
        startActivity(i);
    }

    private void openMenu(View anchor, TaskDto t) {
        selectedTask = t;
        anchor.showContextMenu();
    }

    /** Tạo context menu cho từng item */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Chuyển trạng thái");
        menu.add(0, MENU_TODO, 0, "ToDo");
        menu.add(0, MENU_DOING, 1, "InProgress");
        menu.add(0, MENU_DONE,  2, "Done");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedTask == null) return super.onContextItemSelected(item);
        String newStatus = switch (item.getItemId()) {
            case MENU_TODO -> "ToDo";
            case MENU_DOING -> "InProgress";
            case MENU_DONE -> "Done";
            default -> null;
        };
        if (newStatus == null) return super.onContextItemSelected(item);

        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                tasks.updateStatus(selectedTask.id, newStatus); // giữ nguyên position
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã chuyển sang " + newStatus, Toast.LENGTH_SHORT).show();
                    loadTasks();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        return true;
    }

    /* --- Adapter card nhỏ (nếu bạn đã có TaskAdapter của riêng bạn, chỉ cần thêm longClick callback tương đương) --- */
    static class TaskCardAdapter extends RecyclerView.Adapter<TaskCardVH> {
        interface OnClick { void onClick(TaskDto t); }
        interface OnLong { void onLong(View anchor, TaskDto t); }
        private final List<TaskDto> data;
        private final OnClick onClick;
        private final OnLong onLong;

        TaskCardAdapter(List<TaskDto> d, OnClick c, OnLong l){ data=d; onClick=c; onLong=l; }
        void submit(List<TaskDto> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }

        @Override public TaskCardVH onCreateViewHolder(android.view.ViewGroup parent, int vt) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_task, parent, false);
            return new TaskCardVH(v);
        }
        @Override public void onBindViewHolder(TaskCardVH h, int pos) {
            TaskDto t = data.get(pos);
            h.bind(t);
            h.itemView.setOnClickListener(v -> onClick.onClick(t));
            h.itemView.setOnLongClickListener(v -> {
                v.setOnCreateContextMenuListener((menu, view, info) -> {}); // ensure
                v.showContextMenu();
                onLong.onLong(v, t);
                return true;
            });
            // đăng ký context menu cho từng view (cần cho Activity gọi onCreateContextMenu)
            h.itemView.setOnCreateContextMenuListener((menu, view, info) -> {});
            ((AppCompatActivity) h.itemView.getContext()).registerForContextMenu(h.itemView);
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class TaskCardVH extends RecyclerView.ViewHolder {
        final android.widget.TextView tvTitle, tvDesc, tvStatus, tvDate;
        TaskCardVH(View v){
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDesc  = v.findViewById(R.id.tvDesc);
            tvStatus= v.findViewById(R.id.tvStatus);
            tvDate  = v.findViewById(R.id.tvDate);
        }
        void bind(TaskDto t){
            tvTitle.setText(t.title != null ? t.title : "(no title)");
            tvDesc.setText(t.description == null ? "" : t.description);
            tvStatus.setText(t.status == null ? "ToDo" : t.status);
            tvDate.setText(t.dueDate == null ? "—" : t.dueDate.toString());
        }
    }
}
