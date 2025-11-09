package com.teamapp.ui.widgets;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.teamapp.R;
import com.teamapp.data.dto.ProjectDtos.ProjectDto;
import java.text.SimpleDateFormat;
import java.util.*;

/** Adapter hiển thị danh sách project */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.VH> {

    public interface OnProjectClick {
        void onClick(ProjectDto project);
    }

    private final List<ProjectDto> items;
    private final OnProjectClick listener;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ProjectAdapter(List<ProjectDto> items, OnProjectClick listener) {
        this.items = items;
        this.listener = listener;
    }

    public void submit(List<ProjectDto> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ProjectDto p = items.get(pos);
        h.tvName.setText(p.name);
        h.tvDesc.setText(p.description == null ? "" : p.description);
        h.tvDate.setText(fmt.format(p.createdAt));
        h.tvPrivacy.setText(p.isPublic ? "Public" : "Private");

        h.card.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName, tvDesc, tvDate, tvPrivacy;

        public VH(@NonNull View v) {
            super(v);
            card = v.findViewById(R.id.cardProject);
            tvName = v.findViewById(R.id.tvName);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvDate = v.findViewById(R.id.tvDate);
            tvPrivacy = v.findViewById(R.id.tvPrivacy);
        }
    }
}
