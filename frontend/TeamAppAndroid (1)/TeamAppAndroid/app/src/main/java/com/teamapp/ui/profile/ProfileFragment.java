package com.teamapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.dto.ProfileDtos;
import com.teamapp.data.repo.ProfileRepository;
import com.teamapp.ui.auth.LoginActivity;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvName, tvEmail;
    private Button btnLogout, btnChangePass;
    private SessionStore session;
    private ProfileRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_profile, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        imgAvatar = v.findViewById(R.id.imgAvatar);
        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnChangePass = v.findViewById(R.id.btnChangePass);

        session = App.get().session();
        repo = new ProfileRepository(App.get().retrofit(), session);

        updateUiFromSession();
        refresh();

        btnChangePass.setOnClickListener(v1 ->
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));

        imgAvatar.setOnClickListener(v2 -> {
            // Ví dụ: mở hộp thoại nhập URL avatar
            EditText input = new EditText(requireContext());
            input.setHint("Nhập URL ảnh avatar mới...");

            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Cập nhật Avatar")
                    .setView(input)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        String newAvatarUrl = input.getText().toString().trim();
                        if (newAvatarUrl.isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng nhập URL hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updateAvatar(newAvatarUrl);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        btnLogout.setOnClickListener(v1 -> {
            session.clear(); // Xoá token, user info
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    private void updateUiFromSession() {
        if (session.getUser() != null) {
            ProfileDtos.UserDto user = session.getUser();
            tvName.setText(user.fullName);
            tvEmail.setText(user.email);

            // Dùng Glide để load avatar hoặc hiển thị ảnh mặc định
            if (user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(requireContext())
                        .load(user.avatarUrl)
                        .placeholder(R.drawable.ic_profile_default)
                        .error(R.drawable.ic_profile_default)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_profile_default);
            }
        }
    }



    private void updateAvatar(String newAvatarUrl) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String currentName = tvName.getText().toString();
                ProfileDtos.UpdateProfileRequest body = new ProfileDtos.UpdateProfileRequest(currentName, newAvatarUrl);
                repo.updateProfile(body);

                // Sau khi cập nhật thành công, tải lại thông tin người dùng
                ProfileDtos.UserDto updatedUser = repo.me();
                session.saveUser(updatedUser);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật UI
                    tvName.setText(updatedUser.fullName);
                    tvEmail.setText(updatedUser.email);

                    // Tải lại avatar (nếu có Glide hoặc Picasso)
                    try {
                        com.bumptech.glide.Glide.with(requireContext())
                                .load(updatedUser.avatarUrl)
                                .placeholder(R.drawable.ic_profile)
                                .into(imgAvatar);
                    } catch (Exception ignored) {}
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void refresh() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ProfileDtos.UserDto u = repo.me();
                session.saveUser(u); // Save the updated user data to session
                requireActivity().runOnUiThread(() -> {
                    updateUiFromSession(); // Update UI with the latest data from session
                });
            } catch (Exception ignored) {}
        });
    }
}
