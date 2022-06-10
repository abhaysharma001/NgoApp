package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chetna.ngo.R;

public class OurApproachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_approach);

        getSupportActionBar().setTitle(R.string.our_approach);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
    }
}