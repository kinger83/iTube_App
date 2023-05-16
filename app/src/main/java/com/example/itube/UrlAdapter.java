package com.example.itube;

import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
// Standard Adapter for recycler view
public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {
    private ArrayList<String> urls;
    private  Context context;
    public UrlAdapter(Context context, ArrayList<String> urls) {
        this.urls = urls;
        this.context = context;
    }

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_layout, parent, false);
        return new UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlViewHolder holder, int position) {
        String url = urls.get(position);
        String pos = String.valueOf(position);
        holder.bind(url, pos);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class UrlViewHolder extends RecyclerView.ViewHolder {
        private TextView urlTextView;
        private TextView posView;

        UrlViewHolder(@NonNull View itemView) {
            super(itemView);
            urlTextView = itemView.findViewById(R.id.itemCardUrlText);
            posView = itemView.findViewById(R.id.itemCardPosNum);
        }

        void bind(String url, String pos) {
            urlTextView.setText(url);
            posView.setText("URL: " + pos);
        }
    }
}
