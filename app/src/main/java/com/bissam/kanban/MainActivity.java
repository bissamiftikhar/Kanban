package com.bissam.kanban;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setupTabs();

        findViewById(R.id.fabAddTask).setOnClickListener(v -> {
            new CreateTaskDialog().show(getSupportFragmentManager(), "CreateTask");
        });

        FloatingActionButton fab = findViewById(R.id.fabAddTask);
        fab.setOnClickListener(v -> {
            TodoFragment fragment = (TodoFragment) getSupportFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
            if (fragment != null) {
                fragment.showCreateTaskDialog();
            }
        });
    }

    void setupTabs() {
        BoardPagerAdapter adapter = new BoardPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Todo"); break;
                case 1: tab.setText("In Progress"); break;
                case 2: tab.setText("Done"); break;
            }
        }).attach();
    }
}