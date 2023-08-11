package com.example.wngudfhr;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class info extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView videoRecyclerView;
    private VideoAdapter adapter;
    private List<Listvideo.VideoItem> videoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        db = FirebaseFirestore.getInstance();
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList);
        videoRecyclerView.setAdapter(adapter);

        String userId = getIntent().getStringExtra("userid");

        Query query = db.collection("files").whereEqualTo("userId", userId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        String downloadUrl = documentSnapshot.getString("downloadUrl");
                        Listvideo.VideoItem videoItem = new Listvideo.VideoItem(title, description, downloadUrl);
                        videoList.add(videoItem);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(info.this, "동영상 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(info.this, "동영상 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

        private List<Listvideo.VideoItem> videoList;

        public VideoAdapter(List<Listvideo.VideoItem> videoList) {
            this.videoList = videoList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Listvideo.VideoItem videoItem = videoList.get(position);

            holder.titleTextView.setText(videoItem.getTitle());
            holder.titleTextView.setTypeface(null, Typeface.BOLD);

            holder.descriptionTextView.setText(videoItem.getDescription());
            holder.descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StreamingActivity.class);
                intent.putExtra("downloadUrl", videoItem.getDownloadUrl());
                intent.putExtra("title", videoItem.getTitle());
                intent.putExtra("description", videoItem.getDescription());
                v.getContext().startActivity(intent);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView descriptionTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextViewInfo);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextViewInfo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
