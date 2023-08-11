package com.example.wngudfhr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    private EditText join_name;
    private EditText join_email;
    private Button check_email_button;
    private EditText join_password;
    private EditText join_pwck;
    private Button join_button;
    private Button cancel_button;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); //인스턴스 초기화

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        join_name = findViewById(R.id.join_name);
        join_email = findViewById(R.id.join_email);
        check_email_button = findViewById(R.id.check_email_button);
        join_password = findViewById(R.id.join_password);
        join_pwck = findViewById(R.id.join_pwck);
        join_button = findViewById(R.id.join_button);
        cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(v -> finish());

        check_email_button.setOnClickListener(new View.OnClickListener() {
            //이메일 확인
            final CollectionReference userCollection = db.collection("USER");

            @Override
            public void onClick(View v) {
                String email = join_email.getText().toString().trim();

                userCollection
                        .whereEqualTo("user_id", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "해당 이메일이 중복됩니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        join_button.setOnClickListener(view -> {
            //가입 버튼
            String name = join_name.getText().toString().trim();
            String email = join_email.getText().toString().trim();
            String password = join_password.getText().toString().trim();
            String check_password = join_pwck.getText().toString().trim();
            if (!name.equals("") && !email.equals("") && !password.equals("") && check_password.equals(password)) {
                Map<String, Object> USER = new HashMap<>();
                USER.put("user_id", email);
                USER.put("user_password", password);
                USER.put("user_name", name);

                db.collection("USER")
                        .add(USER)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getApplicationContext(), "가입 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "가입 실패 (서버 오류)", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getApplicationContext(), "비밀번호를  확인하세요!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
