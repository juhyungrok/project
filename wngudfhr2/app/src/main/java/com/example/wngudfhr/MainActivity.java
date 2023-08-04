package com.example.wngudfhr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private EditText etUserid;
    private EditText etPassword;
    private Button loginButton;

    private TextView logintosignup;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private ImageView login_kakao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserid = findViewById(R.id.editID);
        etPassword = findViewById(R.id.ediPassword);
        loginButton = findViewById(R.id.login_login);
        logintosignup = findViewById(R.id.logintosignup);
        login_kakao=findViewById(R.id.btnlogin_kakao);
        login_kakao.setOnClickListener(v -> {


        });
        logintosignup.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(),signup.class);
            startActivity(intent);
        });
        // Firebase Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("USER");
        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        loginButton.setOnClickListener(v -> {
            String email = etUserid.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            usersCollection
                    .whereEqualTo("user_id", email)
                    .whereEqualTo("user_password", password)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                // 세션 정보 저장
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userid", email);
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), login.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "아이디와,비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                Exception exception = task.getException();
                                if (exception != null) {
                                    Toast.makeText(getApplicationContext(), "서버 에러", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //로그인 세션 정보 체크 및 자동 로그인 처리
        String userID = sharedPreferences.getString("userid", null);
        if (userID != null) {
            // 세션 있음, 자동 로그인 처리
            Intent intent =new Intent(getApplicationContext(), login.class);
            intent.putExtra("userid",userID);
            startActivity(intent);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        // onStop() 메서드에서 세션 정보 삭제
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userid");
        editor.apply();
    }
}
