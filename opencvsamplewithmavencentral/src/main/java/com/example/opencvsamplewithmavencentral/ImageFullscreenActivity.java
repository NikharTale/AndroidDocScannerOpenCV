package com.example.opencvsamplewithmavencentral;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        ImageView fullscreenImage = findViewById(R.id.fullscreen_image);

        // Get the file path from the intent
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("image_path");

        if (filePath != null) {
            // Load the image from the file
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            fullscreenImage.setImageBitmap(bitmap);
        }
    }
}

