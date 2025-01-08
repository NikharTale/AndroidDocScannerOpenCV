package com.example.opencvsamplewithmavencentral;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CapturedImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> capturedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_images);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        capturedImages = getIntent().getStringArrayListExtra("captured_images");

        if (capturedImages == null) {
            capturedImages = new ArrayList<>();
        }

        imageAdapter = new ImageAdapter(capturedImages);
        recyclerView.setAdapter(imageAdapter);
    }
}


//package com.example.opencvsamplewithmavencentral;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class CapturedImagesActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_captured_images); // Make sure you have the correct layout
//    }
//}
