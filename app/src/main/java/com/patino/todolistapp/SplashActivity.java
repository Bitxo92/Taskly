package com.patino.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    /**
     * Called when the activity is created.
     *
     * Initializes the views, loads animations, and sets up a sequence of animations to display the splash screen.
     *
     * The sequence consists of three steps:
     * 1. Fade in the app name and icon.
     * 2. Fade in the tagline after a 2-second delay.
     * 3. Transition to the MainActivity after a 3-second delay.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        TextView appName = findViewById(R.id.appName);
        ImageView icon = findViewById(R.id.icon);
        TextView tagline = findViewById(R.id.tagline);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Step 1: Fade in "Taskly" and the icon
        appName.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        appName.startAnimation(fadeIn);
        icon.startAnimation(fadeIn);

        // Step 2: After 2 seconds, fade in the tagline
        new Handler().postDelayed(() -> {
            tagline.setVisibility(View.VISIBLE);
            tagline.startAnimation(fadeIn);
        }, 2000); // 1.5 seconds delay

        // Step 3: After 3 seconds, transition to MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); // Close the splash activity
        }, 3000);
    }
}