package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chetna.ngo.R;

public class TrustiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusties);


        getSupportActionBar().setTitle(R.string.our_trusties);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

    }
}