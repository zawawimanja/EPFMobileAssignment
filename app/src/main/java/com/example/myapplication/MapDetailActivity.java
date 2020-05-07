package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MapDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);


        // Initialize the views.
        TextView sportsTitle = findViewById(R.id.titleDetail);
        TextView sportsAddress = findViewById(R.id.addressTitleDetail);

        // Set the text from the Intent extra.
        sportsTitle.setText(getIntent().getStringExtra("name"));

        sportsAddress.setText(getIntent().getStringExtra("address"));


    }
}
