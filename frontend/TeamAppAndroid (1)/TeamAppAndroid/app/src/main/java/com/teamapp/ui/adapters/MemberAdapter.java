package com.teamapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.data.dto.MemberDtos;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.VH> {

    private final List<MemberDtos.MemberDto> items = new ArrayList<>();

    public void setItems(List<MemberDtos.MemberDto> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MemberDtos.MemberDto m = items.get(position);

        h.tvName.setText(m.fullName != null ? m.fullName : "(no name)");
        h.tvEmail.setText(m.email != null ? m.email : "");
        h.tvRole.setText(m.role != null ? m.role : "Member");

        // Nếu có avatarUrl trong DTO, bạn có thể dùng Glide/Picasso:
        // Glide.with(h.imgAvatar.getContext())
        //      .load(m.avatarUrl)
        //      .placeholder(R.drawable.ic_user_24)
        //      .error(R.drawable.ic_user_24)
        //      .into(h.imgAvatar);
        h.imgAvatar.setImageResource(R.drawable.ic_user_24);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvName;
        final TextView tvEmail;
        final TextView tvRole;

        VH(@NonNull View v) {
            super(v);
            imgAvatar = v.findViewById(R.id.imgAvatar);
            tvName    = v.findViewById(R.id.tvName);
            tvEmail   = v.findViewById(R.id.tvEmail);
            tvRole    = v.findViewById(R.id.tvRole);
        }
    }
}
