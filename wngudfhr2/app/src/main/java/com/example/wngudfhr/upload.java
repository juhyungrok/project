package com.example.wngudfhr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class upload extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Uri selectedVideoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // userid 정보를 받아옴
        String userId = getIntent().getStringExtra("userid");
        if (userId == null) {
            // userid 정보가 없다면 로그인 화면으로 이동
            Toast.makeText(getApplicationContext(), "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {
            // userid 폴더 생성
            StorageReference userRef = storageRef.child(userId);
        }

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        Button chooseButton = findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(v -> openFileChooser());

        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            if (title.isEmpty() || description.isEmpty() || selectedVideoUri == null) {
                Toast.makeText(getApplicationContext(), "제목과 설명을 입력하고 동영상을 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile(selectedVideoUri, title, description, userId);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/*"); // 업로드할 파일 타입 지정
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "동영상 선택"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedVideoUri = data.getData();
            // 파일 선택 후 동영상 정보를 보여줄 수 있으나 여기서는 생략합니다.
        }
    }

    private void uploadFile(Uri fileUri, String title, String description, String userId) {
        StorageReference userRef = storageRef.child(userId);
        StorageReference fileRef = userRef.child(fileUri.getLastPathSegment());

        // 파일 업로드
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 업로드 성공 시 동작
                    Toast.makeText(getApplicationContext(), "파일 업로드 성공", Toast.LENGTH_SHORT).show();
                    //업로드된 파일의 다운로드 URL을 가져옵니다.
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        //다운로드 URL을 사용하여 파일명과 설명을 저장할 수 있습니다.
                       saveFileInformation(title,description,uri.toString(),userId);
                    }) .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(),"URL 가져오기 실패",Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(exception -> {
                    // 업로드 실패 시 동작
                    Toast.makeText(getApplicationContext(), "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveFileInformation(String title, String description, String downloadUrl, String userId) {
        // 파일명과 설명을 저장할 Firestore 또는 Realtime Database를 사용하여 저장하는 로직을 구현합니다.
        // Firestore를 사용하는 예시 코드:
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference filesCollection = db.collection("files");

        // 업로드된 파일 정보를 맵으로 만듭니다.
        Map<String, Object> fileData = new HashMap<>();
        fileData.put("title", title);
        fileData.put("description", description);
        fileData.put("downloadUrl", downloadUrl);
        fileData.put("userId", userId);

        // Firestore에 파일 정보를 저장합니다.
        filesCollection.add(fileData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "파일 정보 저장 성공", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), "파일 정보 저장 실패", Toast.LENGTH_SHORT).show());
}}
