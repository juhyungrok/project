package com.example.navi_layout;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity{
    private EditText nameEditText,emailEditText,passwordEditText,confirmpasswordEditText;
    private Button registerButton;
    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        nameEditText=findViewById(R.id.join_name);
        emailEditText=findViewById(R.id.join_email);
        passwordEditText=findViewById(R.id.join_password);
        confirmpasswordEditText=findViewById(R.id.join_pwck);
        registerButton=findViewById(R.id.join_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmpassword = confirmpasswordEditText.getText().toString().trim();
                if (!isValidEmail(email)) {
                    emailEditText.setError("이메일이 유효하지 않습니다.");
                    emailEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    nameEditText.setError("이름을 입력하세요!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("이메일을 입력하세요!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordEditText.setError(("비밀번호를 입력하세요!"));
                    return;
                }
                if(TextUtils.isEmpty(confirmpassword)){
                    confirmpasswordEditText.setError("비밀번호를 다시 확인하세요.");
                    return;
                }
                if(!password.equals(confirmpassword)){
                    confirmpasswordEditText.setError("비밀번호가 일지하지 않습니다.");
                    return;
                }


                Toast.makeText(getApplicationContext(), "가입완료", Toast.LENGTH_SHORT).show();


                nameEditText.getText().clear();
                emailEditText.getText().clear();
                passwordEditText.getText().clear();
                confirmpasswordEditText.getText().clear();

            }
        });
    }
}
