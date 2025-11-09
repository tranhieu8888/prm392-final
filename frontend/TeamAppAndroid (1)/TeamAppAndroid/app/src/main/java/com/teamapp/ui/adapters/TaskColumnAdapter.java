package com.teamapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.data.entity.TaskEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TaskColumnAdapter extends RecyclerView.Adapter<TaskColumnAdapter.VH> {

    public interface OnTaskClick { void onClick(TaskEntity task); }

    private final List<TaskEntity> items = new ArrayList<>();
    private final OnTaskClick listener;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public TaskColumnAdapter(OnTaskClick listener) {
        this.listener = listener;
    }

    public void setItems(List<TaskEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(TaskEntity task) {
        items.add(task);
        notifyItemInserted(items.size() - 1);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        TaskEntity t = items.get(i);

        h.tvTitle.setText(t.title != null ? t.title : "(no title)");

        h.tvStatus.setText(t.status != null ? t.status : "TODO");

        String due = (t.dueDate != null) ? ("Due: " + dateFmt.format(t.dueDate)) : "";
        h.tvDue.setText(due);

        h.itemView.setOnClickListener(v -> listener.onClick(t));
    }

    @Override public int getItemCount() { return items.size(); }

    private String shortProjectText(UUID id) {
        if (id == null) return "";
        String s = id.toString();
        // Lấy 8 ký tự đầu cho gọn
        return "Project #" + s.substring(0, 8);
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTitle,  tvStatus, tvDue;
        VH(View v) {
            super(v);
            // KHỚP với item_task.xml bạn đã dùng
            tvTitle   = v.findViewById(R.id.tvTitle);

            tvStatus  = v.findViewById(R.id.tvStatus);
            tvDue     = v.findViewById(R.id.tvDesc);
        }
    }
}
