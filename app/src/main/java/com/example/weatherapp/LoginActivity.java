package com.example.weatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView UsernameEditText = findViewById(R.id.UsernameEditText);
        TextView PasswordEditText = findViewById(R.id.PasswordEditText);
        Button SubmitButton = findViewById(R.id.SubmitButton);

        SubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String user = UsernameEditText.getText().toString();
                String pass = PasswordEditText.getText().toString();

                if (user.equals("hassan") && pass.equals("1234")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", user);
                    startActivity(intent);
                    finish();

                }
                else {
                    Toast.makeText(LoginActivity.this, "اطلاعات کاربر یافت نشد.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}