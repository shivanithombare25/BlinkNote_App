package com.example.firstdemoactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationSwitch;
    private Switch darkModeSwitch;
    private Button helpButton;
    private Button aboutButton;

    private SharedPreferences preferences;

    private static final String CHANNEL_ID = "note_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationSwitch = findViewById(R.id.switch_notifications);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        helpButton = findViewById(R.id.button_help);
        aboutButton = findViewById(R.id.button_about);

        preferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        loadSettings();

        // 🌓 Dark mode logic
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // 🔔 Notification logic
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            if (isChecked) {
                showNotification();
            } else {
                Toast.makeText(this, "Notifications Off", Toast.LENGTH_SHORT).show();
            }
        });

        helpButton.setOnClickListener(v ->
                Toast.makeText(this, "For help, contact: support@quicknote.com", Toast.LENGTH_LONG).show());

        aboutButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("About Quick Note")
                        .setMessage("Quick Note v1.0\nDeveloped by Shivani\n© 2025")
                        .setPositiveButton("OK", null)
                        .show());

        createNotificationChannel(); // must be called before showing notification
    }

    private void loadSettings() {
        boolean notifications = preferences.getBoolean("notifications_enabled", true);
        boolean darkMode = preferences.getBoolean("dark_mode_enabled", false);

        notificationSwitch.setChecked(notifications);
        darkModeSwitch.setChecked(darkMode);

        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // or your custom icon
                .setContentTitle("Quick Note")
                .setContentText("Notifications enabled!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NoteChannel";
            String description = "Channel for Quick Note notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
