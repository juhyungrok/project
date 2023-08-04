package com.example.wngudfhr;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Listvideo extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, Object>> videoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.videoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList);
        recyclerView.setAdapter(adapter);

        // Set up SwipeRefreshLayout to handle refresh actions
        swipeRefreshLayout.setOnRefreshListener(this::refreshVideoList);

        // Initially load random 5 video items
        refreshVideoList();
    }

    private void refreshVideoList() {
        // Clear the current video list
        videoList.clear();
        adapter.notifyDataSetChanged();

        // Query the "files" collection to get all video documents
        db.collection("files")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Shuffle the documents
                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                            List<DocumentSnapshot> randomDocuments = getRandomDocuments(documents, 5);

                            // Add the first 5 random documents to the videoList
                            for (DocumentSnapshot documentSnapshot : randomDocuments) {
                                Map<String, Object> fileData = documentSnapshot.getData();
                                if (fileData != null) {
                                    videoList.add(fileData);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "동영상 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "동영상 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    // Complete the swipe refresh animation
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private List<DocumentSnapshot> getRandomDocuments(List<DocumentSnapshot> documents, int count) {
        List<DocumentSnapshot> randomDocuments = new ArrayList<>();
        int totalDocuments = documents.size();

        // Prevent out-of-bounds error
        if (count > totalDocuments) {
            count = totalDocuments;
        }

        Random random = new Random();
        while (randomDocuments.size() < count) {
            int randomIndex = random.nextInt(totalDocuments);
            DocumentSnapshot randomDocument = documents.get(randomIndex);
            if (!randomDocuments.contains(randomDocument)) {
                randomDocuments.add(randomDocument);
            }
        }

        return randomDocuments;
    }

    private class VideoAdapter extends RecyclerView.Adapter<info.VideoAdapter.ViewHolder> {

        private List<Map<String, Object>> videoList;
        private MediaPlayer mediaPlayer;
        private int currentPlayingPosition = -1;
        private MediaController mediaController;

        public VideoAdapter(List<Map<String, Object>> videoList) {
            this.videoList = videoList;
        }

        @NonNull
        @Override
        public info.VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_up, parent, false);
            return new info.VideoAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull info.VideoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Map<String, Object> fileData = videoList.get(position);
            String title = (String) fileData.get("title");
            String description = (String) fileData.get("description");
            String downloadUrl = (String) fileData.get("downloadUrl");

            holder.titleTextView.setText(title);
            holder.descriptionTextView.setText(description);
            holder.videoView.setVideoURI(Uri.parse(downloadUrl));

            holder.videoView.setOnPreparedListener(mp -> {
                mediaPlayer = mp;
                mediaController = new MediaController(Listvideo.this);
                mediaController.setAnchorView(holder.videoView);
                holder.videoView.setMediaController(mediaController);
                holder.videoView.requestFocus();
                holder.videoView.start();
            });

            holder.videoView.setOnCompletionListener(mp -> currentPlayingPosition = -1);

            holder.videoView.setOnClickListener(v -> {
                if (mediaPlayer != null && currentPlayingPosition == position) {
                    // Video is already playing, pause it
                    mediaPlayer.pause();
                } else {
                    // Either video is not playing or the clicked position is different from the current position
                    // In both cases, resume the video and update the currentPlayingPosition
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(downloadUrl);
                        mediaPlayer.setDisplay(holder.videoView.getHolder());
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(mp -> {
                            mediaPlayer.start();
                            currentPlayingPosition = position;
                            toggleFullScreen(holder.videoView);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(holder.itemView.getContext(), "동영상 재생에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }
        private void toggleFullScreen(VideoView videoView) {
            View decorView = getWindow().getDecorView();
            if (videoView.isPlaying()) {
                // Hide the status bar and navigation bar to achieve full-screen
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                // Show the status bar and navigation bar when the video is paused
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTextView;
            private TextView descriptionTextView;
            private VideoView videoView;

            public ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                videoView = itemView.findViewById(R.id.videoView);
            }
        }
    }
}
