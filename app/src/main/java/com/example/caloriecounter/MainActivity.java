package com.example.caloriecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera(View view){
        Toast.makeText(this, "Test button press", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CameraPage.class);
        startActivity(intent);
    }

}