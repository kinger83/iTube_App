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
    // declare variables
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // set up firebase
        binding.homeProgressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        // if no authenticated user, return to login screen
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        // log out button. Logs user out and return to login screen
        binding.homeLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // add url to list button
        binding.homeAddToPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress bar
                binding.homeProgressBar.setVisibility(View.VISIBLE);

                // get url string and ensure its not empty
                String url = binding.homeURLText.getText().toString();
                if(TextUtils.isEmpty(url)){
                    Toast.makeText(HomeActivity.this, "No URL to save", Toast.LENGTH_SHORT).show();
                    return;
                }

                // save url in usermap
                Map<String, Object> urlMap = new HashMap<>();
                urlMap.put("url", url);

                // screate new document under the user with the url
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

        // button to view users play list
        binding.homeViewPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });

        // play button
        binding.homePlayButton.setOnClickListener(new View.OnClickListener() {

            // check url is not empty
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.homeURLText.getText().toString())){
                    Toast.makeText(HomeActivity.this, "No url to play", Toast.LENGTH_SHORT).show();
                    return;
                }

                // load play activity, passing the url to play
                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("url", binding.homeURLText.getText().toString());
                startActivity(intent);
            }
        });
    }
}