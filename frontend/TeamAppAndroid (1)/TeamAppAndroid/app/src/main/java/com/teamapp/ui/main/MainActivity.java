package com.teamapp.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teamapp.R;
import com.teamapp.ui.calendar.CalendarFragment;
import com.teamapp.ui.profile.ProfileFragment;
import com.teamapp.ui.project.ProjectsFragment;
import com.teamapp.ui.search.SearchFragment;
import com.teamapp.ui.task.MyTasksFragment;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SELECTED = "main_selected_item";

    private BottomNavigationView bottomNav;
    private Fragment activeFragment;

    // Back stack đơn giản cho các tab (không phải fragment back stack)
    private final Deque<Integer> tabBackStack = new ArrayDeque<>();

    // Tags để dễ find lại fragments
    private static final String TAG_PROJECTS = "tab_projects";
    private static final String TAG_TASKS    = "tab_tasks";
    private static final String TAG_CAL      = "tab_calendar";
    private static final String TAG_SEARCH   = "tab_search";
    private static final String TAG_PROFILE  = "tab_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootCoordinator), (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Toolbar xuống dưới status bar
            View toolbar = findViewById(R.id.toolbar);
            toolbar.setPadding(
                    toolbar.getPaddingLeft(),
                    toolbar.getPaddingTop() + sysBars.top,
                    toolbar.getPaddingRight(),
                    toolbar.getPaddingBottom()
            );
            // Bottom nav nâng lên khỏi navigation bar
            View bottom = findViewById(R.id.bottomNavigation);
            bottom.setPadding(
                    bottom.getPaddingLeft(),
                    bottom.getPaddingTop(),
                    bottom.getPaddingRight(),
                    bottom.getPaddingBottom() + sysBars.bottom
            );
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNavigation);

        // Tạo hoặc lấy lại fragments theo tag (tránh add trùng sau rotate)
        Fragment projects  = getOrCreate(TAG_PROJECTS, ProjectsFragment::new);
        Fragment tasks     = getOrCreate(TAG_TASKS, MyTasksFragment::new);
        Fragment calendar  = getOrCreate(TAG_CAL, CalendarFragment::new);
        Fragment search    = getOrCreate(TAG_SEARCH, SearchFragment::new);
        Fragment profile   = getOrCreate(TAG_PROFILE, ProfileFragment::new);

        // Add vào container nếu chưa
        ensureAdded(projects, TAG_PROJECTS);
        ensureAdded(tasks, TAG_TASKS);
        ensureAdded(calendar, TAG_CAL);
        ensureAdded(search, TAG_SEARCH);
        ensureAdded(profile, TAG_PROFILE);

        // Ẩn hết, chỉ show tab đang chọn
        int defaultItem = R.id.nav_projects;
        int selectedItem = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_SELECTED, defaultItem)
                : defaultItem;

        showOnly(selectedItem);
        bottomNav.setSelectedItemId(selectedItem);

        // Điều hướng change tab
        bottomNav.setOnItemSelectedListener(item -> {
            showOnly(item.getItemId());
            pushToTabBackStack(item.getItemId());
            return true;
        });

        // Re-select -> cho fragment kéo về đầu nếu hỗ trợ
        bottomNav.setOnItemReselectedListener(item -> {
            Fragment f = findByItemId(item.getItemId());
            if (f instanceof Reselectable) {
                ((Reselectable) f).onTabReselected();
            }
        });

        // Back: quay về tab trước, nếu không còn thì thoát
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (tabBackStack.size() > 1) {
                    // bỏ tab hiện tại
                    tabBackStack.pop();
                    int previous = tabBackStack.peek();
                    bottomNav.setSelectedItemId(previous);
                } else {
                    setEnabled(false);
                    onBackPressed();
                }
            }
        });

        // Khởi tạo back stack lần đầu
        if (savedInstanceState == null) {
            pushToTabBackStack(selectedItem);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_SELECTED, bottomNav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    /* ================= Helpers ================= */

    private interface Reselectable {
        /** Gọi khi người dùng bấm lại icon tab hiện tại (vd: scrollToTop) */
        void onTabReselected();
    }

    private Fragment getOrCreate(String tag, Supplier<Fragment> factory) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        return (f != null) ? f : factory.get();
    }

    private void ensureAdded(Fragment f, String tag) {
        if (!f.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, f, tag)
                    .hide(f)
                    .commitNow();
        }
    }

    private void showOnly(@IdRes int itemId) {
        Fragment target = findByItemId(itemId);
        if (target == null || activeFragment == target) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (activeFragment != null) {
            transaction.hide(activeFragment);
        }
        transaction.show(target).commit();

        activeFragment = target;
    }

    private Fragment findByItemId(@IdRes int itemId) {
        if (itemId == R.id.nav_projects) {
            return getSupportFragmentManager().findFragmentByTag(TAG_PROJECTS);
        } else if (itemId == R.id.nav_tasks) {
            return getSupportFragmentManager().findFragmentByTag(TAG_TASKS);
        } else if (itemId == R.id.nav_calendar) {
            return getSupportFragmentManager().findFragmentByTag(TAG_CAL);
        } else if (itemId == R.id.nav_search) {
            return getSupportFragmentManager().findFragmentByTag(TAG_SEARCH);
        } else if (itemId == R.id.nav_profile) {
            return getSupportFragmentManager().findFragmentByTag(TAG_PROFILE);
        }
        return null;
    }


    private void pushToTabBackStack(@IdRes int itemId) {
        if (tabBackStack.isEmpty() || tabBackStack.peek() != itemId) {
            tabBackStack.push(itemId);
        }
    }

    /* Small utilities */

    private interface Supplier<T> { T get(); }

}
