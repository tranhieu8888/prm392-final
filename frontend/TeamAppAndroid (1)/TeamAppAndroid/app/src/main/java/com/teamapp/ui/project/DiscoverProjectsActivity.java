package com.teamapp.ui.project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.api.JoinRequestApi;
import com.teamapp.data.dto.JoinRequestDtos.JoinRequestDto;
import com.teamapp.data.dto.ProjectDtos;
import com.teamapp.data.repo.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Response;

/** Khám phá dự án Public + Gửi yêu cầu tham gia (dùng ProjectRepository) */
public class DiscoverProjectsActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageButton btnSearch;
    private RecyclerView rv;
    private ProgressBar progress;

    private DiscoverAdapter adapter;
    private ProjectRepository projects;
    private JoinRequestApi joinApi; // có thể bọc tiếp thành JoinRequestRepository nếu bạn muốn

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_projects);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        rv = findViewById(R.id.rvProjects);
        progress = findViewById(R.id.progress);

        projects = ServiceLocator.projects();
        joinApi  = ServiceLocator.retrofit().create(JoinRequestApi.class);

        adapter = new DiscoverAdapter(new ArrayList<>(), this::requestJoin);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> doSearch());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { if (s.length() == 0) doSearch(); }
        });

        doSearch();
    }

    private void doSearch() {
        String q = edtSearch.getText().toString().trim();
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Repo trả List<ProjectDto> và tự cache Room (nếu bạn triển khai trong repo)
                List<ProjectDtos.ProjectDto> list = projects.discover(q);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(list);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void requestJoin(UUID projectId) {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<JoinRequestDto> res = joinApi.request(projectId).execute();
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    if (res.isSuccessful()) {
                        Toast.makeText(this, "Đã gửi yêu cầu tham gia", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gửi yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /* ==== Adapter ==== */
    private static class DiscoverAdapter extends RecyclerView.Adapter<DiscoverVH> {
        interface OnJoin { void click(UUID projectId); }
        private final List<ProjectDtos.ProjectDto> data;
        private final OnJoin onJoin;
        DiscoverAdapter(List<ProjectDtos.ProjectDto> d, OnJoin o){ data=d; onJoin=o; }
        void submit(List<ProjectDtos.ProjectDto> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }

        @Override public DiscoverVH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_project_discover, parent, false);
            return new DiscoverVH(v);
        }
        @Override public void onBindViewHolder(DiscoverVH h, int pos) {
            ProjectDtos.ProjectDto p = data.get(pos);
            h.bind(p);
            h.btnJoin.setOnClickListener(v -> onJoin.click(p.id));
        }
        @Override public int getItemCount() { return data.size(); }
    }
    private static class DiscoverVH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvMeta;
        Button btnJoin;
        DiscoverVH(View v){ super(v);
            tvName = v.findViewById(R.id.tvName);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvMeta = v.findViewById(R.id.tvMeta);
            btnJoin = v.findViewById(R.id.btnJoin);
        }
        void bind(ProjectDtos.ProjectDto p){
            tvName.setText(p.name);
            tvDesc.setText(p.description == null ? "" : p.description);
            tvMeta.setText(p.isPublic ? "Public" : "Private");
        }
    }
}
