package com.teamapp.ui.search;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.teamapp.R;
import com.teamapp.core.util.Debouncer;
import com.teamapp.App;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.repo.ProjectRepository;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.data.dto.ProjectDtos;

import java.util.*;
import retrofit2.*;

public class SearchFragment extends Fragment {

    private EditText edtQuery;
    private ProgressBar progress;
    private RecyclerView recycler;
    private ResultAdapter adapter;

    private final Debouncer debouncer = new Debouncer();
    private ProjectRepository projectRepo;
    private TaskDao taskDao;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_search, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        edtQuery = v.findViewById(R.id.edtQuery);
        progress = v.findViewById(R.id.progress);
        recycler = v.findViewById(R.id.recyclerResults);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResultAdapter();
        recycler.setAdapter(adapter);

        projectRepo = new ProjectRepository(App.get().retrofit(), App.get().db());
        taskDao = App.get().db().taskDao();

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

        projectRepo.discoverAsync(q, new Callback<List<ProjectDtos.ProjectDto>>() {
            @Override
            public void onResponse(Call<List<ProjectDtos.ProjectDto>> call, Response<List<ProjectDtos.ProjectDto>> resp) {
                List<ProjectDtos.ProjectDto> projects = resp.isSuccessful() && resp.body() != null
                        ? resp.body() : new ArrayList<>();

                List<TaskEntity> tasks = filterTasksLocal(q);
                adapter.setResults(projects, tasks);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<ProjectDtos.ProjectDto>> call, Throwable t) {
                List<TaskEntity> tasks = filterTasksLocal(q);
                adapter.setResults(new ArrayList<>(), tasks);
                progress.setVisibility(View.GONE);
            }
        });
    }

    private List<TaskEntity> filterTasksLocal(String q) {
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

    /* ===== Adapter 2 nhóm kết quả ===== */
    static class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_PROJECT = 1;
        private static final int TYPE_TASK = 2;
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

        @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_header, parent, false);
                return new HeaderVH(v);
            } else if (viewType == TYPE_PROJECT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_project, parent, false);
                return new ProjectVH(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_task, parent, false);
                return new TaskVH(v);
            }
        }

        @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
            Object it = items.get(pos);
            if (h instanceof HeaderVH) {
                ((HeaderVH) h).txtTitle.setText(String.valueOf(it));
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
            TextView txtTitle;
            HeaderVH(View v){ super(v); txtTitle = v.findViewById(R.id.txtHeader); }
        }
        static class ProjectVH extends RecyclerView.ViewHolder {
            TextView txtTitle, txtDesc;
            ProjectVH(View v){ super(v); txtTitle=v.findViewById(R.id.txtTitle); txtDesc=v.findViewById(R.id.txtDesc); }
        }
        static class TaskVH extends RecyclerView.ViewHolder {
            TextView txtTitle, txtMeta;
            TaskVH(View v){ super(v); txtTitle=v.findViewById(R.id.txtTitle); txtMeta=v.findViewById(R.id.txtMeta); }
        }
    }
}
