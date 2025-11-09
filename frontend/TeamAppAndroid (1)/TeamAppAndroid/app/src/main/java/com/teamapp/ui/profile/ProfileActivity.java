// app/src/main/java/com/teamapp/ui/profile/ProfileActivity.java
package com.teamapp.ui.profile;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.dto.ProfileDtos;
import com.teamapp.data.repo.AuthRepository;
import com.teamapp.data.repo.ProfileRepository;
import com.teamapp.ui.auth.LoginActivity;

import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvName, tvEmail, tvCreated;
    private Button btnLogout, btnChangePass;

    private SessionStore session;
    private ProfileRepository profileRepo;
    private AuthRepository authRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCreated = findViewById(R.id.tvCreated);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangePass = findViewById(R.id.btnChangePass);

        session = App.get().session();
        profileRepo = new ProfileRepository(App.get().retrofit(), session);
        authRepo = new AuthRepository(App.get().retrofit(), session);

     // Hiển thị nhanh từ session cache (nếu có)
//        var cached = session.getUser();
//        if (cached != null) {
//            tvName.setText(cached.fullName);
//            tvEmail.setText(cached.email);
//        }

        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        btnLogout.setOnClickListener(v -> {
            session.clear(); // Xoá token, user info
            Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            // Xoá toàn bộ stack, tránh quay lại màn cũ
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


        // Refresh từ server -> lưu lại session + update UI
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ProfileDtos.UserDto fresh = profileRepo.me();
                runOnUiThread(() -> {
                    tvName.setText(fresh.fullName);
                    tvEmail.setText(fresh.email);
                    // TODO: load avatarUrl vào imgAvatar (Glide/Picasso) nếu muốn
                });
            } catch (Exception ignored) {
                // giữ UI theo cache nếu lỗi
            }
        });
    }
}
