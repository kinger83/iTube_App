package com.example.itube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.itube.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.homeProgressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        binding.homeLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        binding.homeAddToPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.homeProgressBar.setVisibility(View.VISIBLE);
                String url = binding.homeURLText.getText().toString();
                if(TextUtils.isEmpty(url)){
                    Toast.makeText(HomeActivity.this, "No URL to save", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> urlMap = new HashMap<>();
                urlMap.put("url", url);
                db.collection("users").document(user.getUid()).collection("urls").document()
                        .set(urlMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.homeProgressBar.setVisibility(View.GONE);
                                Toast.makeText(HomeActivity.this, "URL ADDED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.homeProgressBar.setVisibility(View.GONE);
                                Toast.makeText(HomeActivity.this, "Error adding URL", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        binding.homeViewPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });
        binding.homePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.homeURLText.getText().toString())){
                    Toast.makeText(HomeActivity.this, "No url to play", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("url", binding.homeURLText.getText().toString());
                startActivity(intent);
            }
        });
    }
}