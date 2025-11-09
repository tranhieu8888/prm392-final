package com.teamapp.ui.widgets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.data.dto.ConversationDtos;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.VH> {

    public interface OnConversationClick {
        void onClick(ConversationDtos.ConversationDto c);
    }

    private final List<ConversationDtos.ConversationDto> items = new ArrayList<>();
    private final OnConversationClick listener;
    private final DateFormat dateTimeFmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public ConversationAdapter(List<ConversationDtos.ConversationDto> init, OnConversationClick l) {
        if (init != null) items.addAll(init);
        this.listener = l;
    }

    public void submit(List<ConversationDtos.ConversationDto> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false); // cần TextView: tvTitle, tvMeta
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ConversationDtos.ConversationDto c = items.get(position);

        h.tvTitle.setText(c.title != null && !c.title.isEmpty() ? c.title : "(No title)");

        // Meta: loại + số thành viên + thời gian tạo
        int memberCount = (c.members != null) ? c.members.size() : 0;
        String created = (c.createdAt != null) ? dateTimeFmt.format(c.createdAt) : "";
        String type = (c.type != null) ? c.type : "Conversation";
        h.tvMeta.setText(type + " • " + memberCount + " members" + (created.isEmpty() ? "" : " • " + created));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvMeta;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta  = itemView.findViewById(R.id.tvMeta);
        }
    }
}
