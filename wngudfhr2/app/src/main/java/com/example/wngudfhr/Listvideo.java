package com.example.wngudfhr;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Listvideo extends AppCompatActivity {

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firestore;
    private ArrayList<VideoItem> videoList;
    private ArrayAdapter<VideoItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listvideo);

        listView = findViewById(R.id.videoListView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        firestore = FirebaseFirestore.getInstance();
        videoList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, videoList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                VideoItem item = getItem(position);
                if (item != null) {
                    String title = item.getTitle();
                    String description = item.getDescription();
                    text1.setText(title);
                    text1.setTypeface(null, Typeface.BOLD); // 두꺼운 글시체로 설정
                    text2.setText(description);}
                return view;
            }
        };
        listView.setAdapter(adapter);

        loadVideoList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            VideoItem selectedVideo = videoList.get(position);
            Intent intent = new Intent(Listvideo.this, StreamingActivity.class);
            intent.putExtra("downloadUrl", selectedVideo.getDownloadUrl());
            intent.putExtra("title", selectedVideo.getTitle());
            intent.putExtra("description", selectedVideo.getDescription());
            startActivity(intent);
            finish();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadVideoList();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadVideoList() {
        videoList.clear();
        firestore.collection("files")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        Collections.shuffle(documents, new Random());
                        int count = Math.min(documents.size(), 5);
                        for (int i = 0; i < count; i++) {
                            DocumentSnapshot document = documents.get(i);
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String downloadUrl = document.getString("downloadUrl");
                            VideoItem video = new VideoItem(title, description, downloadUrl);
                            videoList.add(video);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public static class VideoItem {
        private String title;
        private String description;
        private String downloadUrl;

        public VideoItem(String title, String description, String downloadUrl) {
            this.title = title;
            this.description = description;
            this.downloadUrl = downloadUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        @NonNull
        @Override
        public String toString() {
            return title + "\n" + description;
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
