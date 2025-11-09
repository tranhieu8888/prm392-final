// com/teamapp/core/util/Debouncer.java
package com.teamapp.core.util;

import android.os.Handler;
import android.os.Looper;

public class Debouncer {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;

    public void debounce(Runnable action, long delayMs) {
        if (pending != null) handler.removeCallbacks(pending);
        pending = action;
        handler.postDelayed(action, delayMs);
    }
}
