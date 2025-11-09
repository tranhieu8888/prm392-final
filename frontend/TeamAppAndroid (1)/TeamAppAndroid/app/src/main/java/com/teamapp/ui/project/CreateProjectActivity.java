package com.teamapp.ui.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.repo.ProjectRepository;

import java.util.concurrent.Executors;

/** Tạo dự án mới (dùng ProjectRepository qua ServiceLocator) */
public class CreateProjectActivity extends AppCompatActivity {

    private EditText edtName, edtDesc;
    private CheckBox cbPublic;
    private Button btnCreate;
    private ProgressBar progress;

    private ProjectRepository projects;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        cbPublic = findViewById(R.id.cbPublic);
        btnCreate = findViewById(R.id.btnCreate);
        progress = findViewById(R.id.progress);

        projects = ServiceLocator.projects();

        btnCreate.setOnClickListener(v -> doCreate());
    }

    private void doCreate() {
        String name = edtName.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        boolean isPublic = cbPublic.isChecked();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nhập tên dự án", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreate.setEnabled(false);
        progress.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                projects.create(name, desc, isPublic); // sẽ tự lưu vào Room trong repo
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnCreate.setEnabled(true);
                    Toast.makeText(this, "Đã tạo dự án", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnCreate.setEnabled(true);
                    Toast.makeText(this, "Tạo thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
