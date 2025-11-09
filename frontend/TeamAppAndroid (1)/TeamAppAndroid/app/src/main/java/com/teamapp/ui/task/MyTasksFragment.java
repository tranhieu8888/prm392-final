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

import com.google.android.material.chip.ChipGroup; // Import cần thiết
import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.api.TaskApi;
import com.teamapp.data.dto.TaskDtos;

import java.util.*;
import java.util.concurrent.Executors;

public class MyTasksFragment extends Fragment {

    private RecyclerView rvTasks;
    private ProgressBar progressBar;
    private TaskListAdapter adapter;
    private TaskApi taskApi;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        // Giả định layout đã đổi tên thành fragment_my_tasks.xml
        return inflater.inflate(R.layout.fragment_my_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        root = view; // Lưu root view để dễ dàng tìm kiếm sau này
        rvTasks = view.findViewById(R.id.recyclerMyTasks); // Đổi tên ID RecyclerView
        progressBar = view.findViewById(R.id.progress); // Giả định ID ProgressBar

        // Khởi tạo các thành phần
        taskApi = App.get().retrofit().create(TaskApi.class);
        adapter = new TaskListAdapter(new ArrayList<>(), this::openTask);

        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTasks.setAdapter(adapter);

        // Khởi tạo bộ lọc (Sử dụng ChipGroup)
        setupStatusFilters(view);

        // Tải task ban đầu (Mặc định không lọc)
        loadMyTasks(null);
    }

    private void setupStatusFilters(@NonNull View view) {
        // Gán listener cho các nút (giả định đã chuyển sang Chip ID theo XML mới)
        view.findViewById(R.id.chipGroupStatus).findViewById(R.id.chipPending).setOnClickListener(v -> loadMyTasks("Pending"));
        view.findViewById(R.id.chipGroupStatus).findViewById(R.id.chipInProgress).setOnClickListener(v -> loadMyTasks("InProgress"));
        view.findViewById(R.id.chipGroupStatus).findViewById(R.id.chipDone).setOnClickListener(v -> loadMyTasks("Done"));

        // Thêm nút "Tất cả" nếu cần, hoặc mặc định loadMyTasks(null) đã là tất cả
        // view.findViewById(R.id.chipAll).setOnClickListener(v -> loadMyTasks(null));
    }

    private void loadMyTasks(@Nullable final String status) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Chuyển sang Thread phụ để gọi API
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Sử dụng tên hàm 'myTasks' cho rõ ràng hơn
                final List<TaskDtos.TaskDto> data = taskApi.myTasks(status, 1, 100).execute().body();

                // Trở lại UI Thread để cập nhật giao diện
                requireActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    final List<TaskDtos.TaskDto> list = (data == null) ? Collections.emptyList() : data;
                    adapter.submit(list);
                });
            } catch (Exception e) {
                // Xử lý lỗi và hiển thị Toast trên UI Thread
                requireActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không tải được tasks: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openTask(final TaskDtos.TaskDto t) {
        final Intent i = new Intent(requireContext(), TaskDetailActivity.class);
        i.putExtra("taskId", t.id.toString());
        i.putExtra("taskTitle", t.title);
        startActivity(i);
    }

    /* Adapter và ViewHolder (Không thay đổi, giữ nguyên) */
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