package com.example.itube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.itube.databinding.ActivityPlayBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PlayActivity extends AppCompatActivity {
    ActivityPlayBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    CollectionReference collectionReference;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        binding.playProgressBar.setVisibility(View.GONE);
        url = getIntent().getStringExtra("url");
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

        binding.playHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        binding.playRemoveFromLIistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.playProgressBar.setVisibility(View.VISIBLE);
                collectionReference = db.collection("users").document(user.getUid()).collection("urls");
                Query query = collectionReference.whereEqualTo("url", url);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                document.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.playProgressBar.setVisibility(View.GONE);
                                                Toast.makeText(PlayActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PlayActivity.this, "Error deleting url", Toast.LENGTH_SHORT).show();
                                                binding.playProgressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        }
                    }
                });


            }
        });
    }
}