// app/src/main/java/com/teamapp/ui/widgets/MessageAdapter.java
package com.teamapp.ui.widgets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.data.dto.MessageDtos;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private final List<MessageDtos.MessageDto> items = new ArrayList<>();
    /** Chỉ khởi tạo khi cần, dùng lại cho các lần bind sau */
    private DateFormat timeFmt = null;

    public MessageAdapter(List<MessageDtos.MessageDto> init) {
        if (init != null) items.addAll(init);
        setHasStableIds(false);
    }

    public void submit(List<MessageDtos.MessageDto> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void add(MessageDtos.MessageDto m) {
        if (m == null) return;
        items.add(m);
        notifyItemInserted(items.size() - 1);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false); // cần TextView: tvBody, tvTime
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MessageDtos.MessageDto m = items.get(position);

        // Lazy init time format từ context của itemView (không bao giờ null)
        if (timeFmt == null) {
            timeFmt = android.text.format.DateFormat.getTimeFormat(h.itemView.getContext());
        }

        h.tvBody.setText(m.body != null ? m.body : "");
        String time = (m.createdAt != null) ? timeFmt.format(m.createdAt) : "";
        h.tvTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvBody;
        final TextView tvTime;
        VH(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
