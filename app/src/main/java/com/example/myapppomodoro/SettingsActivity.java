package com.example.myapppomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton btnCloseSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnCloseSettings = findViewById(R.id.btn_setclose_setact);

        btnCloseSettings.setOnClickListener(v -> {
            finish();
        });
    }
}