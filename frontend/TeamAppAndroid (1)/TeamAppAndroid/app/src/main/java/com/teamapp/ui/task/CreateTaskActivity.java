// com/teamapp/ui/task/CreateTaskActivity.java
package com.teamapp.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamapp.R;
import com.teamapp.core.di.ServiceLocator;
import com.teamapp.data.dto.MemberDtos;
import com.teamapp.data.repo.ProjectRepository;
import com.teamapp.data.repo.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText edtTitle, edtDesc;
    private TextView tvDue, tvAssignees;
    private Button btnPickDue, btnPickAssignees, btnCreate;
    private ProgressBar progress;

    private UUID projectId;
    private TaskRepository tasks;
    private ProjectRepository projects;

    private Date dueDate; // lưu Date người dùng chọn
    private final List<UUID> selectedAssigneeIds = new ArrayList<>();
    private final List<MemberDtos.MemberDto> cachedMembers = new ArrayList<>();

    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc  = findViewById(R.id.edtDesc);
        tvDue    = findViewById(R.id.tvDue);
        tvAssignees = findViewById(R.id.tvAssignees);
        btnPickDue = findViewById(R.id.btnPickDue);
        btnPickAssignees = findViewById(R.id.btnPickAssignees);
        btnCreate = findViewById(R.id.btnCreate);
        progress  = findViewById(R.id.progress);

        String pid = getIntent().getStringExtra("projectId");
        if (!TextUtils.isEmpty(pid)) projectId = UUID.fromString(pid);

        tasks = ServiceLocator.tasks();      // hoặc new TaskRepository(App.get().retrofit(), App.get().db());
        projects = ServiceLocator.projects(); // để load members

        btnPickDue.setOnClickListener(v -> pickDueDateTime());
        btnPickAssignees.setOnClickListener(v -> openAssigneePicker());
        btnCreate.setOnClickListener(v -> doCreate());
    }

    /* ====== Pick due date ====== */
    private void pickDueDateTime() {
        final Calendar cal = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    TimePickerDialog tp = new TimePickerDialog(this,
                            (v1, hh, mm) -> {
                                Calendar c2 = Calendar.getInstance();
                                c2.set(Calendar.YEAR, y);
                                c2.set(Calendar.MONTH, m);
                                c2.set(Calendar.DAY_OF_MONTH, d);
                                c2.set(Calendar.HOUR_OF_DAY, hh);
                                c2.set(Calendar.MINUTE, mm);
                                c2.set(Calendar.SECOND, 0);
                                c2.set(Calendar.MILLISECOND, 0);
                                dueDate = c2.getTime();
                                tvDue.setText(fmt.format(dueDate));
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true);
                    tp.show();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    /* ====== Pick assignees (multi choice) ====== */
    private void openAssigneePicker() {
        progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // lấy danh sách member của project
                List<MemberDtos.MemberDto> members = projects.members(projectId);
                cachedMembers.clear();
                if (members != null) cachedMembers.addAll(members);

                String[] names = new String[cachedMembers.size()];
                boolean[] checked = new boolean[cachedMembers.size()];
                for (int i = 0; i < cachedMembers.size(); i++) {
                    MemberDtos.MemberDto m = cachedMembers.get(i);
                    names[i] = (m.fullName != null && !m.fullName.isEmpty()) ? m.fullName : m.email;
                    checked[i] = selectedAssigneeIds.contains(m.userId);
                }

                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Chọn người được giao")
                            .setMultiChoiceItems(names, checked, (dialog, which, isChecked) -> {
                                UUID uid = cachedMembers.get(which).userId;
                                if (isChecked) {
                                    if (!selectedAssigneeIds.contains(uid)) selectedAssigneeIds.add(uid);
                                } else {
                                    selectedAssigneeIds.remove(uid);
                                }
                            })
                            .setPositiveButton("OK", (d, w) -> renderAssignees())
                            .setNegativeButton("Hủy", null)
                            .show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "Không lấy được thành viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void renderAssignees() {
        if (selectedAssigneeIds.isEmpty()) {
            tvAssignees.setText("—");
            return;
        }
        // map id -> tên ngắn
        List<String> names = new ArrayList<>();
        for (UUID id : selectedAssigneeIds) {
            for (MemberDtos.MemberDto m : cachedMembers) {
                if (m.userId.equals(id)) {
                    names.add(m.fullName != null && !m.fullName.isEmpty() ? m.fullName : m.email);
                    break;
                }
            }
        }
        tvAssignees.setText(TextUtils.join(", ", names));
    }

    /* ====== Create ====== */
    private void doCreate() {
        String title = edtTitle.getText().toString().trim();
        String desc  = edtDesc.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                tasks.create(
                        projectId,
                        title,
                        desc.isEmpty() ? null : desc,
                        dueDate,                                       // Date (có thể null)
                        selectedAssigneeIds.isEmpty() ? null : new ArrayList<>(selectedAssigneeIds)
                );
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnCreate.setEnabled(true);
                    Toast.makeText(this, "Đã tạo task", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    btnCreate.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
