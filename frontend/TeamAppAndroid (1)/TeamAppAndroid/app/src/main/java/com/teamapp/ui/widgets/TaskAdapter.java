package com.teamapp.ui.widgets;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.teamapp.R;
import com.teamapp.data.dto.TaskDtos.TaskDto;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/** Adapter hiển thị danh sách task (Kanban hoặc list view) */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {

    public interface OnTaskClick { void onClick(TaskDto task); }

    private final List<TaskDto> items;
    private final OnTaskClick listener;
    private final SimpleDateFormat outFmt = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public TaskAdapter(List<TaskDto> items, OnTaskClick listener) {
        this.items = items;
        this.listener = listener;
    }

    public void submit(List<TaskDto> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        TaskDto t = items.get(pos);
        h.tvTitle.setText(t.title != null ? t.title : "(no title)");
        h.tvDesc.setText(t.description == null ? "" : t.description);
        h.tvStatus.setText(t.status == null ? "TODO" : t.status);
        h.tvDate.setText(formatDue(t.dueDate));

        h.card.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    /** chấp nhận Date / Long (millis) / String (ISO hoặc y-M-d) / null */
    private String formatDue(Object due) {
        if (due == null) return "—";
        try {
            if (due instanceof Date) {
                return outFmt.format((Date) due);
            } else if (due instanceof Long) {
                return outFmt.format(new Date((Long) due));
            } else if (due instanceof String) {
                String s = (String) due;
                // thử ISO: 2025-11-06T12:34:56Z
                try {
                    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                    iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date d = iso.parse(s);
                    if (d != null) return outFmt.format(d);
                } catch (ParseException ignore) {}
                // thử y-M-d: 2025-11-06
                try {
                    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date d = ymd.parse(s);
                    if (d != null) return outFmt.format(d);
                } catch (ParseException ignore) {}
                // fallback
                return s;
            }
        } catch (Throwable ignore) {}
        return "—";
    }

    static class VH extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView tvTitle, tvDesc, tvStatus, tvDate;

        VH(@NonNull View v) {
            super(v);
            card = v.findViewById(R.id.cardTask);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }
}
