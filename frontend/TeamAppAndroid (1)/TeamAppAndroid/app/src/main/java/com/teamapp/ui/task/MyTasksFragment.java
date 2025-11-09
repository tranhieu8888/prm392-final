package com.teamapp.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.api.TaskApi;
import com.teamapp.data.dto.TaskDtos;

import java.util.*;
import java.util.concurrent.Executors;

public class MyTasksFragment extends Fragment {

    private RecyclerView rv;
    private ProgressBar progress;
    private TaskListAdapter adapter;
    private TaskApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_my_tasks, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        rv = v.findViewById(R.id.rvMyTasks);
        progress = v.findViewById(R.id.progress);

        api = App.get().retrofit().create(TaskApi.class);
        adapter = new TaskListAdapter(new ArrayList<>(), this::openTask);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        loadMyTasks(null);
        v.findViewById(R.id.btnFilterAll).setOnClickListener(vv -> loadMyTasks(null));
        v.findViewById(R.id.btnFilterTodo).setOnClickListener(vv -> loadMyTasks("ToDo"));
        v.findViewById(R.id.btnFilterDoing).setOnClickListener(vv -> loadMyTasks("InProgress"));
        v.findViewById(R.id.btnFilterDone).setOnClickListener(vv -> loadMyTasks("Done"));
    }

    private void loadMyTasks(@Nullable String status) {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // /api/tasks/my?status=...
                List<TaskDtos.TaskDto> data = api.my(status, 1, 100).execute().body();
                if (data == null) data = Collections.emptyList();
                final List<TaskDtos.TaskDto> list = data;
                requireActivity().runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    adapter.submit(list);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không tải được tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void openTask(TaskDtos.TaskDto t) {
        Intent i = new Intent(requireContext(), TaskDetailActivity.class);
        i.putExtra("taskId", t.id.toString());
        i.putExtra("taskTitle", t.title);
        startActivity(i);
    }

    /* Adapter tối giản */
    static class TaskListAdapter extends RecyclerView.Adapter<TaskVH> {
        interface OnClick { void onClick(TaskDtos.TaskDto t); }
        private final List<TaskDtos.TaskDto> data;
        private final OnClick onClick;
        TaskListAdapter(List<TaskDtos.TaskDto> d, OnClick o){ data=d; onClick=o; }
        void submit(List<TaskDtos.TaskDto> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }

        @NonNull @Override public TaskVH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_task_my, p, false);
            return new TaskVH(v);
        }
        @Override public void onBindViewHolder(@NonNull TaskVH h, int pos) {
            TaskDtos.TaskDto t = data.get(pos);
            h.bind(t);
            h.itemView.setOnClickListener(v -> onClick.onClick(t));
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class TaskVH extends RecyclerView.ViewHolder {
        final android.widget.TextView tvTitle, tvMeta;
        TaskVH(View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvMeta=v.findViewById(R.id.tvMeta); }
        void bind(TaskDtos.TaskDto t){
            tvTitle.setText(t.title != null ? t.title : "(no title)");
            String st = t.status != null ? t.status : "ToDo";
            tvMeta.setText("[" + st + "] " + (t.description==null?"":t.description));
        }
    }
}
