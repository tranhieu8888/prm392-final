// app/src/main/java/com/teamapp/ui/calendar/CalendarFragment.java
package com.teamapp.ui.calendar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.teamapp.App;
import com.teamapp.R;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.data.entity.TaskEntity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {

    private TextView txtSelectedDate, btnPickDate;
    private RecyclerView rv;
    private ProgressBar progress;
    private DayTaskAdapter adapter;
    private TaskDao taskDao;

    private final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_calendar, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        txtSelectedDate = v.findViewById(R.id.txtSelectedDate);
        btnPickDate     = v.findViewById(R.id.btnPickDate);
        rv              = v.findViewById(R.id.rvTasksByDay);
        progress        = v.findViewById(R.id.progress);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DayTaskAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        taskDao = App.get().db().taskDao();

        String today = dayFmt.format(Calendar.getInstance().getTime());
        txtSelectedDate.setText(today);
        loadForDay(today);

        btnPickDate.setOnClickListener(v1 -> pickDate());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (DatePicker view, int y, int m, int d) -> {
            String mm = (m + 1 < 10 ? "0" + (m + 1) : String.valueOf(m + 1));
            String dd = (d < 10 ? "0" + d : String.valueOf(d));
            String sel = y + "-" + mm + "-" + dd;
            txtSelectedDate.setText(sel);
            loadForDay(sel);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** CHẠY DAO Ở BACKGROUND (KHÔNG block UI) */
    private void loadForDay(String ymd) {
        if (!isAdded()) return;
        progress.setVisibility(View.VISIBLE);

        io.execute(() -> {
            List<TaskEntity> list;
            try {
                list = taskDao.byDayString(ymd);
            } catch (Throwable t) {
                list = Collections.emptyList();
            }

            if (!isAdded()) return;
            final List<TaskEntity> result = list; // <-- thêm dòng này

            requireActivity().runOnUiThread(() -> {
                adapter.submit(result);
                progress.setVisibility(View.GONE);
                if (result.isEmpty()) {
                    Toast.makeText(getContext(), "Không có task trong ngày " + ymd, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    /* Adapter tối giản */
    static class DayTaskAdapter extends RecyclerView.Adapter<VH> {
        private final List<TaskEntity> items;
        DayTaskAdapter(List<TaskEntity> items) { this.items = items; }
        void submit(List<TaskEntity> list) { items.clear(); if (list != null) items.addAll(list); notifyDataSetChanged(); }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_task_my, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int i) {
            TaskEntity t = items.get(i);
            h.tvTitle.setText(t.title != null ? t.title : "(no title)");
            String st = t.status != null ? t.status : "ToDo";
            h.tvMeta.setText(st);
        }
        @Override public int getItemCount() { return items.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta;
        VH(View v) { super(v); tvTitle = v.findViewById(R.id.tvTitle); tvMeta = v.findViewById(R.id.tvMeta); }
    }
}
