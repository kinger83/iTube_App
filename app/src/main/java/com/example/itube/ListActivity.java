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
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    ActivityListBinding binding;
    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.listProgressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        populateUrls();

        binding.listBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });



    }

    private void populateUrls(){
        binding.listProgressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(user.getUid()).collection("urls")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> urls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String url = document.getString("url");
                            urls.add(url);
                        }

                        binding.listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        UrlAdapter urlAdapter = new UrlAdapter(this,urls);
                        binding.listRecyclerView.setAdapter(urlAdapter);
                        binding.listProgressBar.setVisibility(View.GONE);
                    } else {
                        // Handle the error
                        Toast.makeText(getApplicationContext(), "Error retrieving URLs", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}