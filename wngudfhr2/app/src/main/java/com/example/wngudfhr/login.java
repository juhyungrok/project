package com.example.wngudfhr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    private Button item_info;
    private Button item_logout;
    private Button item_video;
    private Button item_upload;
    private Button item_hello;
    private TextView login_success;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        item_info = findViewById(R.id.item_info);
        item_logout = findViewById(R.id.item_logout);
        item_video = findViewById(R.id.item_video);
        item_upload = findViewById(R.id.item_upload);
        item_hello = findViewById(R.id.item_hello);
        login_success = findViewById(R.id.login_success);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);

        View.OnClickListener clickListener = this::handleClick;
        item_info.setOnClickListener(clickListener);
        item_logout.setOnClickListener(clickListener);
        item_video.setOnClickListener(clickListener);
        item_upload.setOnClickListener(clickListener);
        item_hello.setOnClickListener(clickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // onStart() 메서드에서 세션 정보 체크
        checkSession();
    }

    @SuppressLint("SetTextI18n")
    private void checkSession() {
        String userId = sharedPreferences.getString("userid", null);
        if (userId != null) {
            // 세션 정보가 있으면 사용자를 자동으로 로그인시킨다.
            login_success.setText(userId + "님! 환영합니다.");

            //로그인 성공 시 userid 정보를 업로드 화면으로 전달
            item_upload.setOnClickListener(v -> {
                Toast.makeText(login.this,"업로드",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), upload.class);
                intent.putExtra("userid",userId);
                startActivity(intent);
            });
            item_info.setOnClickListener(v -> {
                Intent intent=new Intent(getApplicationContext(), info.class);
                intent.putExtra("userid",userId);
                startActivity(intent);
            });
            item_video.setOnClickListener(v -> {
                Intent intent=new Intent(getApplicationContext(), Listvideo.class);
                intent.putExtra("userid",userId);
                startActivity(intent);
            });
        }
    }

    private void handleClick(View v) {
        if (v.getId() == R.id.item_logout) {
            // Handle item_info click
            Toast.makeText(login.this, "로그아웃", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove("userid");
            editor.apply();
            finish(); //로그인 화면으로 이동
        } else if (v.getId() == R.id.item_info) {
            // Handle item_login click
            Toast.makeText(login.this, "내 정보", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.item_upload) {
            // Handle item_setting click
            Toast.makeText(login.this, "업로드", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.item_video) {
            // Handle item_upload click
            Toast.makeText(login.this, "추천 동영상", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.item_hello) {
            // Handle item_sign click
            Toast.makeText(login.this, "개발자", Toast.LENGTH_SHORT).show();

        }
    }


}
