package com.teamapp.ui.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamapp.R;
import com.teamapp.core.db.dao.ProjectDao;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.entity.ProjectEntity;
import com.teamapp.data.repo.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/** Danh sách dự án của tôi (Room observed) */
public class ProjectsActivity extends AppCompatActivity {

    private static final String TAG = "ProjectsActivity";

    private RecyclerView rv;
    private ProgressBar progress;
    private FloatingActionButton fab;

    private ProjectAdapter adapter;
    private ProjectRepository projects;
    private ProjectDao projectDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        rv = findViewById(R.id.rvProjects);
        progress = findViewById(R.id.progress);
        fab = findViewById(R.id.fabAdd);

        projects = ServiceLocator.projects();
        projectDao = ServiceLocator.db().projectDao();

        adapter = new ProjectAdapter(new ArrayList<>(), item -> {
            Intent i = new Intent(this, MembersActivity.class);
            i.putExtra("projectId", item.id.toString());
            i.putExtra("projectName", item.name);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(this, CreateProjectActivity.class)));

        // Quan sát Room để tự update UI
        projectDao.all().observe(this, new Observer<List<ProjectEntity>>() {
            @Override
            public void onChanged(List<ProjectEntity> projectEntities) {
                adapter.submit(projectEntities);
            }
        });

        // Tải “My Projects” từ server → repo tự ghi Room
        loadMyProjects();
    }

    private void loadMyProjects() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                android.util.Log.d(TAG, "fetchMyProjects() bắt đầu...");
                projects.fetchMyProjects(); // repo gọi API và upsert Room
                android.util.Log.d(TAG, "fetchMyProjects() xong, tắt progress");
                runOnUiThread(() -> progress.setVisibility(View.GONE));
            } catch (IOException e) {
                android.util.Log.e(TAG, "fetchMyProjects() lỗi", e);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /* ==== Adapter dùng ProjectEntity, KHỚP item_project.xml ==== */
    private static class ProjectAdapter extends RecyclerView.Adapter<ProjectVH> {
        interface OnClick { void onClick(ProjectEntity item); }

        private final List<ProjectEntity> data;
        private final OnClick onClick;

        ProjectAdapter(List<ProjectEntity> data, OnClick onClick) {
            this.data = data;
            this.onClick = onClick;
        }

        void submit(List<ProjectEntity> list) {
            data.clear();
            if (list != null) data.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public ProjectVH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_project, parent, false);
            return new ProjectVH(v);
        }

        @Override
        public void onBindViewHolder(ProjectVH h, int pos) {
            ProjectEntity p = data.get(pos);
            h.bind(p);
            h.itemView.setOnClickListener(v -> onClick.onClick(p));
        }

        @Override
        public int getItemCount() { return data.size(); }
    }

    private static class ProjectVH extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvName, tvDesc, tvDate, tvPrivacy;

        ProjectVH(View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvName);
            tvDesc    = itemView.findViewById(R.id.tvDesc);
            tvDate    = itemView.findViewById(R.id.tvDate);
            tvPrivacy = itemView.findViewById(R.id.tvPrivacy);
        }

        void bind(ProjectEntity p) {
            tvName.setText(p.name != null ? p.name : "");
            tvDesc.setText(p.description != null ? p.description : "");
            tvPrivacy.setText(p.isPublic ? "Public" : "Private");

            // Format Date → String
            if (p.createdAt != null) {
                java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                tvDate.setText(fmt.format(p.createdAt));
            } else {
                tvDate.setText("");
            }
        }
    }

}
