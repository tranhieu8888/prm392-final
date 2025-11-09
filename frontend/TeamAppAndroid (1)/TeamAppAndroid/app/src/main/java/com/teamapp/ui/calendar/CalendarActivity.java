// com/teamapp/ui/calendar/CalendarActivity.java
package com.teamapp.ui.calendar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamapp.R;
import com.teamapp.App;
import com.teamapp.core.db.AppDb;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.data.entity.TaskEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView txtSelectedDate, btnPickDate;
    private RecyclerView recycler;
    private ProgressBar progress;

    private TaskAdapter adapter;
    private TaskDao taskDao;

    private final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_calendar);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        btnPickDate     = findViewById(R.id.btnPickDate);
        recycler        = findViewById(R.id.recyclerTasks);
        progress        = findViewById(R.id.progress);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter();
        recycler.setAdapter(adapter);

        // Lấy DAO từ AppDb (App.get() là Application bạn đã set up)
        AppDb db = App.get().db();
        taskDao = db.taskDao();

        String today = dayFmt.format(new Date());
        txtSelectedDate.setText(today);
        loadForDay(today);

        btnPickDate.setOnClickListener(v -> pickDate());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) -> {
                    String mm = (m + 1 < 10 ? "0" + (m + 1) : String.valueOf(m + 1));
                    String dd = (d < 10 ? "0" + d : String.valueOf(d));
                    String sel = y + "-" + mm + "-" + dd;
                    txtSelectedDate.setText(sel);
                    loadForDay(sel);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /** Tải & lọc theo ngày (chạy gọn trên background thread cho đỡ block UI) */
    private void loadForDay(String ymd) {
        progress.setVisibility(View.VISIBLE);

        new Thread(() -> {
            // ymd -> millis (local)
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            fmt.setTimeZone(java.util.TimeZone.getDefault()); // local
            long anyMs = 0;
            try {
                anyMs = fmt.parse(ymd).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            long[] bounds = dayBoundsLocal(anyMs);
            List<TaskEntity> tasks = taskDao.byDayRange(bounds[0], bounds[1]);

            List<TaskEntity> match = new ArrayList<>();
            for (TaskEntity t : tasks) {
                if (t.dueDate == null) continue;                 // Room lưu kiểu Date
                if (isSameDay(t.dueDate, ymd)) match.add(t);
            }

            runOnUiThread(() -> {
                adapter.setItems(match);
                progress.setVisibility(View.GONE);
                if (match.isEmpty()) {
                    Toast.makeText(this, "Không có task trong ngày " + ymd, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    // Utils: tính đầu/cuối ngày theo múi giờ thiết bị
    public static long[] dayBoundsLocal(long anyMsInDay) {
        java.util.Calendar cal = java.util.Calendar.getInstance(); // local timezone
        cal.setTimeInMillis(anyMsInDay);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        long end = cal.getTimeInMillis();

        return new long[]{ start, end };
    }

    /** So sánh Date (UTC/local) theo chuỗi yyyy-MM-dd đã chọn */
    private boolean isSameDay(Date dueDate, String ymd) {
        try {
            String d = dayFmt.format(dueDate);
            return d.equals(ymd);
        } catch (Exception e) {
            return false;
        }
    }

    /* ================= Adapter ================= */
    static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
        private final List<TaskEntity> items = new ArrayList<>();
        private final SimpleDateFormat metaFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        void setItems(List<TaskEntity> list) {
            items.clear();
            if (list != null) items.addAll(list);
            notifyDataSetChanged();
        }

        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_calendar_task, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(VH h, int i) {
            TaskEntity t = items.get(i);
            h.txtTitle.setText(t.title != null ? t.title : "(no title)");

            String dueText = (t.dueDate != null) ? metaFmt.format(t.dueDate) : "";
            String status  = (t.status != null) ? t.status : "TODO";
            h.txtMeta.setText("Due: " + dueText + " • " + status);
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView txtTitle, txtMeta;
            VH(View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txtTitle);
                txtMeta  = itemView.findViewById(R.id.txtMeta);
            }
        }
    }
}
