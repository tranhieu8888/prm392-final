// app/src/main/java/com/teamapp/ui/widgets/NotificationAdapter.java
package com.teamapp.ui.widgets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.data.entity.NotificationEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    public interface OnItemClick {
        void onClick(@NonNull NotificationEntity n);
        void onMarkClick(@NonNull NotificationEntity n);
    }

    private final List<NotificationEntity> items = new ArrayList<>();
    private final OnItemClick listener;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public NotificationAdapter(@NonNull OnItemClick l) {
        this.listener = l;
    }

    public void submit(List<NotificationEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void markReadLocal(UUID id) {
        for (int i = 0; i < items.size(); i++) {
            NotificationEntity n = items.get(i);
            if (n.id.equals(id)) {
                n.isRead = true;
                notifyItemChanged(i);
                return;
            }
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        NotificationEntity n = items.get(i);
        h.txtTitle.setText(mapTitle(n.type));
        h.txtBody.setText(n.dataJson != null ? n.dataJson : "");
        h.txtTime.setText(n.createdAt != null ? fmt.format(n.createdAt) : "");

        h.dotUnread.setVisibility(n.isRead ? View.INVISIBLE : View.VISIBLE);
        h.btnMark.setImageResource(n.isRead ? R.drawable.ic_check_24 : R.drawable.ic_mark_email_read_24);

        h.itemView.setOnClickListener(v -> listener.onClick(n));
        h.btnMark.setOnClickListener(v -> listener.onMarkClick(n));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtBody, txtTime;
        ImageView btnMark, dotUnread;
        VH(@NonNull View v) {
            super(v);
            txtTitle = v.findViewById(R.id.tvTitle);
            txtBody  = v.findViewById(R.id.tvBody);
            txtTime  = v.findViewById(R.id.tvTime);
            btnMark  = v.findViewById(R.id.btnMark);
            dotUnread= v.findViewById(R.id.dotUnread);
        }
    }

    private String mapTitle(String type) {
        if (type == null) return "Thông báo";
        switch (type) {
            case "TaskAssigned": return "Bạn được giao task";
            case "TaskUpdated":  return "Task đã cập nhật";
            case "CommentAdded": return "Có bình luận mới";
            case "Invite":       return "Lời mời tham gia dự án";
            default:             return "Thông báo";
        }
    }
}
