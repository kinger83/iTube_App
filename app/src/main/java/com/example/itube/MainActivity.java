package com.example.itube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.itube.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    // declare variables
    ActivityMainBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up binding
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.mainProgressBar.setVisibility(View.GONE);
        // setup firebase
        mAuth = FirebaseAuth.getInstance();

        // login button
        binding.mainLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mainProgressBar.setVisibility(View.VISIBLE);
                // get inputs and check they are not empty
                String email = binding.mainEmailText.getText().toString();
                String password = binding.mainPasswordText.getText().toString();
                if(!validateInput(email, password)){
                    binding.mainProgressBar.setVisibility(View.GONE);
                    return;
                }
                // sign in with firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // if successful, send user to their home page
                                    binding.mainProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    // else, alert using to failed login
                                    binding.mainProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




            }
        });

        // button to sign up
        binding.mainSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // check inputs
    private Boolean validateInput(String email, String password){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}