// app/src/main/java/com/teamapp/ui/profile/ChangePasswordActivity.java
package com.teamapp.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.data.repo.ProfileRepository;

import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOld, edtNew, edtConfirm;
    private Button btnChange;
    private ProgressBar progress;
    private ProfileRepository profileRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOld = findViewById(R.id.edtOldPass);
        edtNew = findViewById(R.id.edtNewPass);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnChange = findViewById(R.id.btnChange);
        progress = findViewById(R.id.progress);

        profileRepo = new ProfileRepository(App.get().retrofit(), App.get().session());

        btnChange.setOnClickListener(v -> doChange());
    }

    private void doChange() {
        String oldP = edtOld.getText().toString().trim();
        String newP = edtNew.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(oldP) || TextUtils.isEmpty(newP)) {
            Toast.makeText(this, "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newP.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnChange.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                profileRepo.changePassword(oldP, newP);
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnChange.setEnabled(true);
                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnChange.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
