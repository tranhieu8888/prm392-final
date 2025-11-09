package com.teamapp.ui.widgets;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** Decorator thêm khoảng cách đều giữa các item RecyclerView */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public SpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = space;
    }
}
