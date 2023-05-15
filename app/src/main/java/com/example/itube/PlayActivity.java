package com.example.itube;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.itube.databinding.ActivityPlayBinding;

public class PlayActivity extends AppCompatActivity {
    ActivityPlayBinding binding;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        url = getIntent().getStringExtra("url");
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }
}