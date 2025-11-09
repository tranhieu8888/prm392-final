package com.teamapp.ui.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.db.dao.ProjectDao;
import com.teamapp.data.entity.ProjectEntity;
import com.teamapp.data.repo.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProjectsFragment extends Fragment {

    private RecyclerView rv;
    private ProgressBar progress;
    private ProjectAdapter adapter;
    private ProjectRepository repo;
    private ProjectDao projectDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_projects, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        rv = v.findViewById(R.id.rvProjects);
        progress = v.findViewById(R.id.progress);

        repo = new ProjectRepository(App.get().retrofit(), App.get().db());
        projectDao = App.get().db().projectDao();

        adapter = new ProjectAdapter(new ArrayList<>(), item -> {
            Intent i = new Intent(requireContext(), MembersActivity.class);
            i.putExtra("projectId", item.id.toString());
            i.putExtra("projectName", item.name);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        projectDao.all().observe(getViewLifecycleOwner(), new Observer<List<ProjectEntity>>() {
            @Override public void onChanged(List<ProjectEntity> projectEntities) {
                adapter.submit(projectEntities);
            }
        });

        v.findViewById(R.id.fabAdd).setOnClickListener(view ->
                startActivity(new Intent(requireContext(), CreateProjectActivity.class))
        );

        loadMyProjects();
    }

    private void loadMyProjects() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                repo.fetchMyProjects();
                requireActivity().runOnUiThread(() -> progress.setVisibility(View.GONE));
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không tải được dự án: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /* ==== Adapter tối giản ==== */
    private static class ProjectAdapter extends RecyclerView.Adapter<ProjectVH> {
        interface OnClick { void onClick(ProjectEntity item); }
        private final List<ProjectEntity> data;
        private final OnClick onClick;
        ProjectAdapter(List<ProjectEntity> data, OnClick onClick) { this.data = data; this.onClick = onClick; }
        void submit(List<ProjectEntity> list) { data.clear(); if (list!=null) data.addAll(list); notifyDataSetChanged(); }

        @NonNull @Override public ProjectVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
            return new ProjectVH(v);
        }
        @Override public void onBindViewHolder(@NonNull ProjectVH h, int pos) {
            ProjectEntity p = data.get(pos);
            h.bind(p);
            h.itemView.setOnClickListener(v -> onClick.onClick(p));
        }
        @Override public int getItemCount() { return data.size(); }
    }
    private static class ProjectVH extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvName, tvDesc, tvMeta;
        ProjectVH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvMeta = itemView.findViewById(R.id.tvMeta);
        }
        void bind(ProjectEntity p){
            tvName.setText(p.name);
            tvDesc.setText(p.description == null ? "" : p.description);
            if (tvMeta != null) {
                tvMeta.setText(p.isPublic ? "Public" : "Private");
            }
        }
    }
}
