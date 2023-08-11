package com.example.wngudfhr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingActivity extends AppCompatActivity {

    private EditText commentEditText;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;

    private FirebaseFirestore firestore;
    private List<Comment> commentList;

    private String downloadUrl;
    private String title;
    private String description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        VideoView videoView = findViewById(R.id.videoView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        commentEditText = findViewById(R.id.commentEditText);
        Button postCommentButton = findViewById(R.id.postCommentButton);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        downloadUrl = getIntent().getStringExtra("downloadUrl");
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        // Load comments
        loadComments();

        // Post a comment
        postCommentButton.setOnClickListener(v -> postComment());

        // Load and play the video
        videoView.setVideoPath(downloadUrl);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Set video layout parameters for fixed size and maintaining aspect ratio
        videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            float videoProportion = (float) videoWidth / (float) videoHeight;

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // Calculate the target dimensions while maintaining aspect ratio
            int targetWidth;
            int targetHeight;
            if (videoWidth > videoHeight) {
                targetWidth = screenWidth;
                targetHeight = (int) (targetWidth / videoProportion);
            } else {
                targetHeight = screenHeight;
                targetWidth = (int) (targetHeight * videoProportion);
            }

            // Apply the calculated dimensions to the VideoView
            ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
            layoutParams.width = targetWidth;
            layoutParams.height = targetHeight;
            videoView.setLayoutParams(layoutParams);

            videoView.start();
        });
    }





    @SuppressLint("NotifyDataSetChanged")
    private void loadComments() {
        commentList.clear();
        firestore.collection("comment")
                .whereEqualTo("downloadUrl", downloadUrl)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String comment = document.getString("comment");
                            commentList.add(new Comment(comment));
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void postComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (!commentText.isEmpty()) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("downloadUrl", downloadUrl);
            commentMap.put("comment", commentText);

            firestore.collection("comment").add(commentMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            commentEditText.setText("");
                            loadComments();
                        }
                    });
        }
    }

    private static class Comment {
        private String comment;

        public Comment(String comment) {
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }
    }

    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

        private List<Comment> commentList;

        public CommentAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.commentTextView.setText(comment.getComment());
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView commentTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
