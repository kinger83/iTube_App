package com.example.itube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.itube.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding.signupProgressBar.setVisibility(View.GONE);

        binding.signupBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        binding.signupSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signupProgressBar.setVisibility(View.VISIBLE);
                String name = binding.signupFirstNameText.getText().toString();
                String sName = binding.signupSurnameText.getText().toString();
                String password = binding.signupPasswordText.getText().toString();
                String confirmPassword = binding.signupConfirmPassword.getText().toString();
                String email = binding.signupEmailText.getText().toString();
                if(!validateInput(name, sName, password, confirmPassword, email)) return;

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", name);
                                    userMap.put("sName", sName);
                                    userMap.put("email", email);
                                    userMap.put("id", user.getUid());

                                    db.collection("users").document(user.getUid())
                                            .set(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SignupActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                                                    binding.signupProgressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    binding.signupProgressBar.setVisibility(View.GONE);
                                                    Toast.makeText(SignupActivity.this, "Error signing up", Toast.LENGTH_SHORT).show();
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    user.delete();
                                                }
                                            });

                                }
                                else{
                                    binding.signupProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "Error signing up", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });
    }

    private Boolean validateInput(String name, String sName, String password, String confirmPassword, String email){
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(sName)){
            Toast.makeText(this, "Please enter your surname", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please enter the confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!TextUtils.equals(password, confirmPassword)){
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}