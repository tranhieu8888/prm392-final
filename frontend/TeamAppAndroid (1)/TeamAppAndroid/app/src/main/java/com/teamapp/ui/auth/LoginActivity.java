package com.teamapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.repo.AuthRepository;
import com.teamapp.ui.project.ProjectsActivity;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progress;

    private AuthRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progress = findViewById(R.id.progress);

        repo = ServiceLocator.auth();

        // ✅ Chỉ auto vào Projects nếu TOKEN CÒN HẠN
        if (ServiceLocator.session().hasValidToken()) {
            startActivity(new Intent(this, com.teamapp.ui.main.MainActivity.class));
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> tryLogin());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void tryLogin() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AuthDtos.AuthResponse res = repo.login(email, pass); // phải setToken() THÔ trong repo
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Đăng nhập thành công, chào " + res.user.fullName, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, com.teamapp.ui.main.MainActivity.class));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
