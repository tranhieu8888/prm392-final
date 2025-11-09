// app/src/main/java/com/teamapp/ui/search/GlobalSearchActivity.java
package com.teamapp.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.core.util.Debouncer;
import com.teamapp.data.dto.ProjectDtos;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.data.repo.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class GlobalSearchActivity extends AppCompatActivity {

    private EditText edtQuery;
    private ProgressBar progress;
    private RecyclerView recycler;
    private ResultAdapter adapter;

    private final Debouncer debouncer = new Debouncer();
    private ProjectRepository projectRepo;
    private TaskDao taskDao;

    @Override
    protected void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_global_search);

        edtQuery = findViewById(R.id.edtQuery);
        progress = findViewById(R.id.progress);
        recycler  = findViewById(R.id.recyclerResults);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultAdapter();
        recycler.setAdapter(adapter);

        // ServiceLocator cần trả về ProjectRepository & AppDb
        projectRepo = ServiceLocator.projects();
        taskDao = ServiceLocator.db().taskDao();

        edtQuery.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                debouncer.debounce(() -> doSearch(s.toString().trim()), 300);
            }
        });
    }

    private void doSearch(String q) {
        if (q.isEmpty()) {
            adapter.setResults(new ArrayList<>(), new ArrayList<>());
            return;
        }
        progress.setVisibility(View.VISIBLE);

        // Chạy nền: gọi repo discover (sync) + query TaskDao local
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ProjectDtos.ProjectDto> projects = new ArrayList<>();
            List<TaskEntity> tasks;

            try {
                // Nếu bạn đã bổ sung discoverSync trong ProjectRepository:
                projects = projectRepo.discoverSync(q);
            } catch (Exception e) {
                // ignore -> để projects rỗng, vẫn hiển thị tasks local
            }

            // Ưu tiên DAO có hàm LIKE; nếu chưa có, fallback filter bằng Java
            try {
                tasks = taskDao.searchByText("%" + q + "%"); // yêu cầu bạn đã thêm @Query LIKE
            } catch (Throwable ignore) {
                tasks = filterTasksLocalFallback(q);
            }

            List<ProjectDtos.ProjectDto> finalProjects = projects;
            List<TaskEntity> finalTasks = tasks;
            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                adapter.setResults(finalProjects, finalTasks);
            });
        });
    }

    // Fallback nếu DAO chưa có LIKE
    private List<TaskEntity> filterTasksLocalFallback(String q) {
        List<TaskEntity> all = taskDao.getAll();
        List<TaskEntity> out = new ArrayList<>();
        String qq = q.toLowerCase();
        for (TaskEntity t : all) {
            String title = t.title != null ? t.title.toLowerCase() : "";
            String desc = t.description != null ? t.description.toLowerCase() : "";
            if (title.contains(qq) || desc.contains(qq)) out.add(t);
        }
        return out;
    }

    /* ================= Adapter: Projects + Tasks ================= */

    static class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER  = 0;
        private static final int TYPE_PROJECT = 1;
        private static final int TYPE_TASK    = 2;

        private final List<Object> items = new ArrayList<>();

        void setResults(List<ProjectDtos.ProjectDto> projects, List<TaskEntity> tasks) {
            items.clear();
            items.add("Projects");
            items.addAll(projects);
            items.add("Tasks");
            items.addAll(tasks);
            notifyDataSetChanged();
        }

        @Override public int getItemViewType(int position) {
            Object it = items.get(position);
            if (it instanceof String) return TYPE_HEADER;
            if (it instanceof ProjectDtos.ProjectDto) return TYPE_PROJECT;
            return TYPE_TASK;
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inf = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_HEADER) {
                View v = inf.inflate(R.layout.item_search_header, parent, false);
                return new HeaderVH(v);
            } else if (viewType == TYPE_PROJECT) {
                View v = inf.inflate(R.layout.item_search_project, parent, false);
                return new ProjectVH(v);
            } else {
                View v = inf.inflate(R.layout.item_search_task, parent, false);
                return new TaskVH(v);
            }
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int pos) {
            Object it = items.get(pos);
            if (h instanceof HeaderVH) {
                ((HeaderVH) h).txtHeader.setText(String.valueOf(it));
            } else if (h instanceof ProjectVH) {
                ProjectDtos.ProjectDto p = (ProjectDtos.ProjectDto) it;
                ((ProjectVH) h).txtTitle.setText(p.name != null ? p.name : "(no name)");
                ((ProjectVH) h).txtDesc.setText(p.description != null ? p.description : "");
            } else if (h instanceof TaskVH) {
                TaskEntity t = (TaskEntity) it;
                ((TaskVH) h).txtTitle.setText(t.title != null ? t.title : "(no title)");
                String st = t.status != null ? t.status : "TODO";
                ((TaskVH) h).txtMeta.setText("[" + st + "] " + (t.description != null ? t.description : ""));
            }
        }

        @Override public int getItemCount() { return items.size(); }

        static class HeaderVH extends RecyclerView.ViewHolder {
            TextView txtHeader;
            HeaderVH(View v) { super(v); txtHeader = v.findViewById(R.id.txtHeader); }
        }
        static class ProjectVH extends RecyclerView.ViewHolder {
            TextView txtTitle, txtDesc;
            ProjectVH(View v) {
                super(v);
                txtTitle = v.findViewById(R.id.txtTitle);
                txtDesc  = v.findViewById(R.id.txtDesc);
            }
        }
        static class TaskVH extends RecyclerView.ViewHolder {
            TextView txtTitle, txtMeta;
            TaskVH(View v) {
                super(v);
                txtTitle = v.findViewById(R.id.txtTitle);
                txtMeta  = v.findViewById(R.id.txtMeta);
            }
        }
    }
}
