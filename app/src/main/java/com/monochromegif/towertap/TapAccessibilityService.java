package com.monochromegif.towertap;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class TapAccessibilityService extends AccessibilityService {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean tapInProgress = false;
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            long due = getSharedPreferences("towertap", MODE_PRIVATE).getLong("due", 0);
            if (due > 0 && System.currentTimeMillis() >= due && !tapInProgress) {
                getSharedPreferences("towertap", MODE_PRIVATE).edit().putLong("due", 0).apply();
                int x = getSharedPreferences("towertap", MODE_PRIVATE).getInt("x", 500);
                int y = getSharedPreferences("towertap", MODE_PRIVATE).getInt("y", 1000);
                performTap(x, y);
            }
            handler.postDelayed(this, 100);
        }
    };
    @Override protected void onServiceConnected() { super.onServiceConnected(); handler.post(ticker); }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) { /* Intentionally does not inspect app content. */ }
    @Override public void onInterrupt() { getSharedPreferences("towertap", MODE_PRIVATE).edit().putLong("due", 0).apply(); }
    @Override public void onDestroy() { handler.removeCallbacks(ticker); super.onDestroy(); }
    private void performTap(float x, float y) {
        Path path = new Path(); path.moveTo(x, y);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 80);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        tapInProgress = true;
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override public void onCompleted(GestureDescription g) { tapInProgress = false; }
            @Override public void onCancelled(GestureDescription g) { tapInProgress = false; }
        }, null);
    }
}
