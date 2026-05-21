package com.bissam.kanban;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etCollabEmail;
    private TextView tvStatus, tvCollaborators, tvMoveToLabel;
    private Button btnUpdate, btnDelete, btnToTodo, btnToInProgress, btnToDone, btnClose;
    private View layoutStatusButtons;
    private String taskId;
    private DatabaseReference taskRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        etTitle = findViewById(R.id.etDetailTitle);
        etDescription = findViewById(R.id.etDetailDescription);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvCollaborators = findViewById(R.id.tvCollaboratorsList);
        etCollabEmail = findViewById(R.id.etCollaboratorEmail);
        btnUpdate = findViewById(R.id.btnUpdateTask);
        btnDelete = findViewById(R.id.btnDeleteTask);
        
        tvMoveToLabel = findViewById(R.id.tvMoveToLabel);
        layoutStatusButtons = findViewById(R.id.layoutStatusButtons);
        btnToTodo = findViewById(R.id.btnToTodo);
        btnToInProgress = findViewById(R.id.btnToInProgress);
        btnToDone = findViewById(R.id.btnToDone);
        btnClose = findViewById(R.id.btnCloseDetail);

        taskId = getIntent().getStringExtra("taskId");
        currentUserId = FirebaseAuth.getInstance().getUid();

        if (taskId == null || currentUserId == null) {
            finish();
            return;
        }

        taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);

        loadTaskDetails();

        findViewById(R.id.btnAddCollaborator).setOnClickListener(v -> addCollaborator());
        btnToTodo.setOnClickListener(v -> updateStatus("todo"));
        btnToInProgress.setOnClickListener(v -> updateStatus("inprogress"));
        btnToDone.setOnClickListener(v -> updateStatus("done"));
        
        btnUpdate.setOnClickListener(v -> updateTaskContent());
        btnDelete.setOnClickListener(v -> deleteTask());
        btnClose.setOnClickListener(v -> finish());

        btnUpdate.setVisibility(View.VISIBLE);
    }

    private void loadTaskDetails() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Task task = snapshot.getValue(Task.class);
                if (task != null) {
                    if (etTitle.getText().toString().isEmpty() || !etTitle.hasFocus()) {
                        etTitle.setText(task.title);
                    }
                    if (etDescription.getText().toString().isEmpty() || !etDescription.hasFocus()) {
                        etDescription.setText(task.description);
                    }
                    tvStatus.setText("Status: " + task.status);

                    updateStatusButtons(task.status);

                    if (currentUserId.equals(task.ownerUid)) {
                        btnDelete.setVisibility(View.VISIBLE);
                    } else {
                        btnDelete.setVisibility(View.GONE);
                    }

                    if (task.collaborators != null && !task.collaborators.isEmpty()) {
                        fetchCollaboratorNames(new ArrayList<>(task.collaborators.keySet()));
                    } else {
                        tvCollaborators.setText("Only you");
                    }
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateStatusButtons(String status) {
        if ("todo".equals(status)) {
            tvMoveToLabel.setVisibility(View.VISIBLE);
            layoutStatusButtons.setVisibility(View.VISIBLE);
            btnToTodo.setVisibility(View.GONE);
            btnToInProgress.setVisibility(View.VISIBLE);
            btnToDone.setVisibility(View.VISIBLE);
        } else if ("inprogress".equals(status)) {
            tvMoveToLabel.setVisibility(View.VISIBLE);
            layoutStatusButtons.setVisibility(View.VISIBLE);
            btnToTodo.setVisibility(View.GONE);
            btnToInProgress.setVisibility(View.GONE);
            btnToDone.setVisibility(View.VISIBLE);
        } else if ("done".equals(status)) {
            tvMoveToLabel.setVisibility(View.GONE);
            layoutStatusButtons.setVisibility(View.GONE);
        } else {
            // Default or unexpected status
            tvMoveToLabel.setVisibility(View.VISIBLE);
            layoutStatusButtons.setVisibility(View.VISIBLE);
            btnToTodo.setVisibility(View.VISIBLE);
            btnToInProgress.setVisibility(View.VISIBLE);
            btnToDone.setVisibility(View.VISIBLE);
        }
    }

    private void fetchCollaboratorNames(List<String> uids) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        final List<String> namesList = new ArrayList<>();
        
        for (String uid : uids) {
            usersRef.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null) {
                        namesList.add(name);
                    }
                    if (namesList.size() > 0) {
                        tvCollaborators.setText(TextUtils.join(", ", namesList));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void addCollaborator() {
        String email = etCollabEmail.getText().toString().trim();
        if (email.isEmpty()) return;

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String uid = userSnap.getKey();
                        if (uid != null) {
                            if (uid.equals(currentUserId)) {
                                Toast.makeText(TaskDetailActivity.this, "You are already the owner", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            taskRef.child("collaborators").child(uid).setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(TaskDetailActivity.this, "Collaborator added!", Toast.LENGTH_SHORT).show();
                                        etCollabEmail.setText("");
                                    });
                        }
                    }
                } else {
                    Toast.makeText(TaskDetailActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTaskContent() {
        String newTitle = etTitle.getText().toString().trim();
        String newDesc = etDescription.getText().toString().trim();

        if (newTitle.isEmpty()) {
            etTitle.setError("Title cannot be empty");
            return;
        }

        taskRef.child("title").setValue(newTitle);
        taskRef.child("description").setValue(newDesc).addOnSuccessListener(aVoid -> 
            Toast.makeText(TaskDetailActivity.this, "Task updated", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateStatus(String newStatus) {
        taskRef.child("status").setValue(newStatus).addOnSuccessListener(aVoid -> 
            Toast.makeText(TaskDetailActivity.this, "Moved to " + newStatus, Toast.LENGTH_SHORT).show()
        );
    }

    private void deleteTask() {
        taskRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(TaskDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}