package com.example.itube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayActivity extends AppCompatActivity {
    // Declare variables
    ActivityPlayBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    CollectionReference collectionReference;
    String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up binding
        binding = ActivityPlayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // setup firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        // hide loading bar
        binding.playProgressBar.setVisibility(View.GONE);
        //get url from previous activity
        url = getIntent().getStringExtra("url");
        // setup web view
        webView = findViewById(R.id.playWebView);
        setupWebView();

        // I cant work out how to get whole url to play.. so I extract the video id from the url
        String videoId = extractVideoId(url);
        String videoUrl = "https://www.youtube.com/embed/" + videoId;

        // write a html string to load into web viewer
        String html = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + videoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        // load webview with html string
        webView.loadData(html, "text/html", "utf-8");

        // button to return to home screen
        binding.playHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        // button to delete url from list
        binding.playRemoveFromLIistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make progress bar visible
                binding.playProgressBar.setVisibility(View.VISIBLE);
                // find the document containing the current url
                collectionReference = db.collection("users").document(user.getUid()).collection("urls");
                Query query = collectionReference.whereEqualTo("url", url);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // if url is found, delete url
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                document.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // if delete is successful, remove progress bar, return to home
                                                binding.playProgressBar.setVisibility(View.GONE);
                                                Toast.makeText(PlayActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                startActivity(intent);
                                            }
                                            // if fails, alert user
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
        // button to return to list screen
        binding.playListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });
    }

    // private method to extract the video id. Copied from Stack overflow
    private String extractVideoId(String youtubeUrl) {
        String videoId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|v=|\\/v\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|\\/v%2F|^youtu.be\\/|watch\\?v=|\\?v=|\\&v=|\\/watch\\?v=)([^#\\&\\?\\n]*[^\\&\\?\\n])";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }
}