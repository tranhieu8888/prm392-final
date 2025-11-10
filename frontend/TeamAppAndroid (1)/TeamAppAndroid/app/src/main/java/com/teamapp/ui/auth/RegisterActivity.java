package com.teamapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns; // Sử dụng import trực tiếp cho Patterns
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Thêm annotation cho tính chuyên nghiệp
import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.repo.AuthRepository;
import com.teamapp.ui.main.MainActivity; // Import đúng Main Activity

import java.util.concurrent.Executors;

/**
 * Màn hình đăng ký tài khoản người dùng (Màn hình 2).
 * - Xử lý xác thực đầu vào và gửi request tới AuthRepository.
 * - Lưu JWT & user vào SessionStore và chuyển sang MainActivity sau khi đăng ký thành công.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPassword, edtConfirm;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progress;

    private AuthRepository repo;

    // Đặt hằng số ở phạm vi lớp
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khai báo ánh xạ View
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progress = findViewById(R.id.progress);

        // Khởi tạo Repository
        repo = new AuthRepository(App.get().retrofit(), App.get().session());

        // Thiết lập Listener
        btnRegister.setOnClickListener(v -> tryRegister());
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void tryRegister() {
        // Lấy giá trị và dọn dẹp (trim)
        final String name = edtFullName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String pass = edtPassword.getText().toString().trim();
        final String confirm = edtConfirm.getText().toString().trim();

        // 1. Kiểm tra trống
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra định dạng email (Sử dụng Patterns đã import)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Kiểm tra độ dài mật khẩu
        if (pass.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Kiểm tra khớp mật khẩu
        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bắt đầu quá trình gọi API (Hiển thị ProgressBar)
        progress.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Chuyển sang luồng phụ (Worker Thread) để gọi Retrofit
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AuthDtos.AuthResponse res = repo.register(name, email, pass);

                // Trở lại UI Thread để cập nhật giao diện
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký thành công, chào " + res.user.fullName, Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
                    startActivity(new Intent(this, MainActivity.class));
                    finish(); // Kết thúc RegisterActivity
                });
            } catch (Exception e) {
                // Xử lý lỗi và hiển thị trên UI Thread
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Dùng LENGTH_LONG cho lỗi
                });
            }
        });
    }
}