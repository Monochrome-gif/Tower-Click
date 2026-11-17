package com.monochromegif.towertap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText xInput, yInput, secondsInput;
    private SharedPreferences prefs;
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("towertap", MODE_PRIVATE);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setBackgroundColor(0xFF101827);

        TextView title = new TextView(this); title.setText("TowerTap"); title.setTextSize(30); title.setTypeface(null, Typeface.BOLD); title.setTextColor(0xFFFFFFFF);
        root.addView(title);
        TextView sub = new TextView(this); sub.setText("A simple, user-controlled screen tap timer"); sub.setTextColor(0xFFB8C4D8); sub.setTextSize(14); sub.setPadding(0, dp(6), 0, dp(22)); root.addView(sub);

        xInput = field("Tap X coordinate", String.valueOf(prefs.getInt("x", 500))); root.addView(xInput);
        yInput = field("Tap Y coordinate", String.valueOf(prefs.getInt("y", 1000))); root.addView(yInput);
        secondsInput = field("Countdown in seconds", "5"); root.addView(secondsInput);

        Button permission = button("1. Enable accessibility service"); root.addView(permission);
        permission.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        Button schedule = button("2. Schedule one tap"); root.addView(schedule);
        schedule.setOnClickListener(v -> scheduleTap());
        Button stop = button("Stop / cancel tap"); root.addView(stop);
        stop.setOnClickListener(v -> { prefs.edit().putLong("due", 0).apply(); Toast.makeText(this, "Tap cancelled", Toast.LENGTH_SHORT).show(); });

        TextView note = new TextView(this);
        note.setText("How to use: enable TowerTap in Android Accessibility settings, return here, enter the screen coordinates, then schedule a tap. Keep the target app open. Use only where automation is allowed. This tool does not predict game outcomes.");
        note.setTextColor(0xFFB8C4D8); note.setTextSize(13); note.setPadding(0, dp(18), 0, 0); root.addView(note);
        setContentView(root);
    }

    private EditText field(String hint, String value) {
        EditText e = new EditText(this); e.setSingleLine(true); e.setHint(hint); e.setText(value); e.setTextColor(0xFFFFFFFF); e.setHintTextColor(0xFF93A4BA); e.setPadding(dp(14), dp(8), dp(14), dp(8));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(54)); p.bottomMargin = dp(12); e.setLayoutParams(p); e.setBackgroundColor(0xFF243247); return e;
    }
    private Button button(String text) { Button b = new Button(this); b.setText(text); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(54)); p.bottomMargin = dp(10); b.setLayoutParams(p); return b; }
    private void scheduleTap() {
        try {
            int x = Integer.parseInt(xInput.getText().toString().trim());
            int y = Integer.parseInt(yInput.getText().toString().trim());
            int seconds = Integer.parseInt(secondsInput.getText().toString().trim());
            if (x < 0 || y < 0 || seconds < 1 || seconds > 3600) throw new NumberFormatException();
            prefs.edit().putInt("x", x).putInt("y", y).putLong("due", System.currentTimeMillis() + seconds * 1000L).apply();
            Toast.makeText(this, "One tap scheduled. Keep TowerTap accessibility enabled.", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException ex) { Toast.makeText(this, "Enter valid coordinates and 1–3600 seconds.", Toast.LENGTH_SHORT).show(); }
    }
}
