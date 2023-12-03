package com.example.securebank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  FirebaseApp.initializeApp(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        TextView welcomeText = findViewById(R.id.welcometxt);
        Button startButton = findViewById(R.id.startBtn);

        String fullText = welcomeText.getText().toString();

        SpannableStringBuilder spannable = new SpannableStringBuilder(fullText);

        int start = fullText.indexOf("Secure");
        int end = start + "Secure".length();

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#6DCB6B")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setText(spannable);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
