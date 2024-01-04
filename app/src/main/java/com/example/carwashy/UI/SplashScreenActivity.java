package com.example.carwashy.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        VideoView videoView = findViewById(R.id.videoView);

        // Set the path of the video file
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.splash_screen;

        // Set the URI of the video to the VideoView
        videoView.setVideoURI(Uri.parse(videoPath));

        // Remove the default media controller
        videoView.setMediaController(null);

        // Start playing the video
        videoView.start();

        // Set a completion listener to handle the end of the video
        videoView.setOnCompletionListener(mediaPlayer -> {
            // Navigate to the main activity when the video ends
            Intent intent = new Intent(SplashScreenActivity.this, HomePage.class);
            startActivity(intent);
            finish(); // Close the splash screen activity
        });
    }
}
