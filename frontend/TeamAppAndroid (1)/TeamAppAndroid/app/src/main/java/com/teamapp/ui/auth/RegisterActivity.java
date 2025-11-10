package com.teamapp.ui.auth;

import static com.teamapp.R.*;
import static com.teamapp.R.id.edtFullName;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.repo.AuthRepository;
import com.teamapp.ui.project.ProjectsActivity;

import java.util.concurrent.Executors;

/**
 * Màn hình đăng ký tài khoản người dùng.
 * - Gửi request tới AuthRepository
 * - Lưu JWT & user vào SessionStore
 * - Tự động chuyển sang ProjectsActivity sau khi đăng ký
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPassword, edtConfirm;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progress;

    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progress = findViewById(R.id.progress);

        repo = new AuthRepository(App.get().retrofit(), App.get().session());

        btnRegister.setOnClickListener(v -> tryRegister());
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void tryRegister() {
        String name = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        // 1. Kiểm tra trống
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra khớp mật khẩu
        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Kiểm tra định dạng email (BỔ SUNG)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AuthDtos.AuthResponse res = repo.register(name, email, pass);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký thành công, chào " + res.user.fullName, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, com.teamapp.ui.main.MainActivity.class));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
