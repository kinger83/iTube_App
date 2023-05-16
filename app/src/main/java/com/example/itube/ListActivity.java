package com.example.itube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.itube.databinding.ActivityListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    // declare variables
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    ActivityListBinding binding;
    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up binding
        binding = ActivityListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.listProgressBar.setVisibility(View.GONE);
        // set up firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        // if no authenticated user, return to login page
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        // populate the url list
        populateUrls();

        // back button
        binding.listBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });



    }

    private void populateUrls(){
        // set progress bar visible
        binding.listProgressBar.setVisibility(View.VISIBLE);
        // retrieve all urls from user
        db.collection("users").document(user.getUid()).collection("urls")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // if successful, load urls into url list for recycler view
                        ArrayList<String> urls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String url = document.getString("url");
                            urls.add(url);
                        }

                        // setup and display recycler view
                        binding.listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        UrlAdapter urlAdapter = new UrlAdapter(this,urls);
                        binding.listRecyclerView.setAdapter(urlAdapter);
                        binding.listProgressBar.setVisibility(View.GONE);
                    } else {
                        // Handle the error
                        Toast.makeText(getApplicationContext(), "Error retrieving URLs", Toast.LENGTH_SHORT).show();
                        binding.listProgressBar.setVisibility(View.GONE);
                    }
                });

    }
}