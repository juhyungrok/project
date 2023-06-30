package com.example.navi_layout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class loginscreen extends AppCompatActivity {
    TextView sign;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //회원가입
        sign=findViewById(R.id.signin);

        //회원가입 버튼 클릭 시,회원가입 페이지로 이동
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });
    }
}
