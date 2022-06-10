package com.chetna.ngo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chetna.ngo.R;
import com.chetna.ngo.fragments.AddSliderFragment;
import com.chetna.ngo.fragments.AssignSubAdminFragment;
import com.chetna.ngo.fragments.CreateSubAdminFragment;

public class OpenFragmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_fragments);
        String title = "";
        Fragment fragment = null;
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "create_sub_admin":
                title = getString(R.string.create_sub_admin);
                fragment = CreateSubAdminFragment.newInstance();
                break;
            case "assign_sub_admin":
                title = getString(R.string.assign_sub_admin);
                fragment = AssignSubAdminFragment.newInstance(getIntent().getStringExtra("project_id"),getIntent().getStringExtra("project_name"));
                break;
                case "Change Slider Images":
                title = getString(R.string.change_slider_images);
                fragment = AddSliderFragment.newInstance(getIntent().getStringExtra("project_id"));
                break;
        }

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        replaceFragment(fragment);
    }


    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}