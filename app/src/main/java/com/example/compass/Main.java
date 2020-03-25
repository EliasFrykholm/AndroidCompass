package com.example.compass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {
    private Button compassButton;
    private Button accelerometerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassButton = findViewById(R.id.compassButton);
        compassButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Compass.class);
                startActivity(intent);
            }
        });

        accelerometerButton = findViewById(R.id.accelerometerButton);
        accelerometerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Accelerometer.class);
                startActivity(intent);
            }
        });
    }
}
