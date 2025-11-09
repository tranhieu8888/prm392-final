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
    private Button btnLogout, btnChangePass, btnRefresh;
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
        btnRefresh = v.findViewById(R.id.btnRefresh);

        session = App.get().session();
        repo = new ProfileRepository(App.get().retrofit(), session);

//        if (session.getUser() != null) {
//            tvName.setText(session.getUser().fullName);
//            tvEmail.setText(session.getUser().email);
//        }

        btnChangePass.setOnClickListener(v1 ->
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));

        btnLogout.setOnClickListener(v1 -> {
            session.clear(); // Xoá token, user info
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


        btnRefresh.setOnClickListener(v13 -> refresh());
    }

    private void refresh() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ProfileDtos.UserDto u = repo.me();
                requireActivity().runOnUiThread(() -> {
                    tvName.setText(u.fullName);
                    tvEmail.setText(u.email);
                });
            } catch (Exception ignored) {}
        });
    }
}
