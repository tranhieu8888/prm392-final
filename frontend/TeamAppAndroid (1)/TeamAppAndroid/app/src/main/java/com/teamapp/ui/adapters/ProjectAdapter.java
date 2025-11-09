package com.teamapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.teamapp.R;
import com.teamapp.data.dto.ProjectDtos.ProjectDto;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.VH> {

    public interface OnProjectClick { void onClick(ProjectDto project); }

    private final List<ProjectDto> items;
    private final OnProjectClick listener;
    private final SimpleDateFormat outFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ProjectAdapter(List<ProjectDto> items, OnProjectClick listener) {
        this.items = items;
        this.listener = listener;
    }

    public void submit(List<ProjectDto> newList) {
        items.clear();
        if (newList != null) items.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ProjectDto p = items.get(pos);
        h.tvName.setText(p.name != null ? p.name : "(no name)");
        h.tvDesc.setText(p.description == null ? "" : p.description);
        h.tvPrivacy.setText(Boolean.TRUE.equals(p.isPublic) ? "Public" : "Private");

        h.tvDate.setText(formatCreatedAt(p.createdAt));

        h.card.setOnClickListener(v -> {
            if (listener != null) listener.onClick(p);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    private String formatCreatedAt(Object createdAt) {
        if (createdAt == null) return "";
        try {
            if (createdAt instanceof Date) {
                return outFmt.format((Date) createdAt);
            } else if (createdAt instanceof Long) {
                return outFmt.format(new Date((Long) createdAt));
            } else if (createdAt instanceof String) {
                // thử parse ISO-8601 đơn giản
                String s = (String) createdAt;
                // dạng "2025-11-06T12:34:56Z"
                try {
                    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                    iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date d = iso.parse(s);
                    return d != null ? outFmt.format(d) : s;
                } catch (ParseException ignore) { }
                // dạng "2025-11-06"
                try {
                    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date d = ymd.parse(s);
                    return d != null ? outFmt.format(d) : s;
                } catch (ParseException ignore) { }
                // fallback: trả chuỗi gốc
                return s;
            }
        } catch (Throwable ignore) { }
        return "";
    }

    static class VH extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView tvName, tvDesc, tvDate, tvPrivacy;

        VH(@NonNull View v) {
            super(v);
            card = v.findViewById(R.id.cardProject);
            tvName = v.findViewById(R.id.tvName);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvDate = v.findViewById(R.id.tvDate);
            tvPrivacy = v.findViewById(R.id.tvPrivacy);
        }
    }
}
