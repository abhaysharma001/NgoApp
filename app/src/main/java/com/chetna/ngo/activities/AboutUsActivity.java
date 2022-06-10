package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chetna.ngo.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);



        getSupportActionBar().setTitle(R.string.about_us);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
    }
}