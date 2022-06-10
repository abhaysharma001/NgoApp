package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityWorkingAreaInfoBinding;

public class WorkingAreaInfoActivity extends AppCompatActivity {

    private Context context;
    private ActivityWorkingAreaInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkingAreaInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.bViewStudentList.setOnClickListener(view -> context.startActivity(new Intent(context, ChildListActivity.class)
                .putExtra("name", getIntent().getStringExtra("name"))
                .putExtra("working_area_id", getIntent().getStringExtra("working_area_id"))));

        binding.bAddWorkingAreaAttendance.setOnClickListener(view -> {
            startActivity(new Intent(context, AttendanceActivity.class)
                    .putExtra("working_area_id", getIntent().getStringExtra("working_area_id")));
        });

    }
}