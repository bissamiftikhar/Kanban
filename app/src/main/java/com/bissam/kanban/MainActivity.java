package com.bissam.kanban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fab = findViewById(R.id.fabAddTask);

        setupTabs();

        fab.setOnClickListener(v -> showCreateTaskDialog());

        // Hide FAB in other tabs
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });
    }

    private void showCreateTaskDialog() {
        CreateTaskDialog dialog = new CreateTaskDialog();
        dialog.setOnTaskSaveListener(this::saveTaskToFirebase);
        dialog.show(getSupportFragmentManager(), "CreateTaskDialog");
    }

    private void saveTaskToFirebase(String title, String description) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks");
        String taskId = ref.push().getKey();

        FirebaseUser user = auth.getCurrentUser();
        String uid = (user != null) ? user.getUid() : "unknown";
        String name = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Anonymous";

        Task newTask = new Task(taskId, title, description, "todo", uid, name);

        if (taskId != null) {
            ref.child(taskId).setValue(newTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Task created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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