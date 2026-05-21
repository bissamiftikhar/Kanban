package com.bissam.kanban;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvStatus, tvDescription, tvCollaborators;
    private EditText etCollabEmail;
    private String taskId;
    private DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvCollaborators = findViewById(R.id.tvCollaboratorsList);
        etCollabEmail = findViewById(R.id.etCollaboratorEmail);

        taskId = getIntent().getStringExtra("taskId");
        if (taskId == null) {
            finish();
            return;
        }

        taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);

        loadTaskDetails();

        findViewById(R.id.btnAddCollaborator).setOnClickListener(v -> addCollaborator());
        findViewById(R.id.btnToTodo).setOnClickListener(v -> updateStatus("todo"));
        findViewById(R.id.btnToInProgress).setOnClickListener(v -> updateStatus("inprogress"));
        findViewById(R.id.btnToDone).setOnClickListener(v -> updateStatus("done"));
    }

    private void loadTaskDetails() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Task task = snapshot.getValue(Task.class);
                if (task != null) {
                    tvTitle.setText(task.title);
                    tvDescription.setText(task.description);
                    tvStatus.setText("Status: " + task.status);

                    if (task.collaborators != null && !task.collaborators.isEmpty()) {
                        fetchCollaboratorNames(new ArrayList<>(task.collaborators.keySet()));
                    } else {
                        tvCollaborators.setText("No collaborators yet");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchCollaboratorNames(List<String> uids) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        StringBuilder names = new StringBuilder();
        
        for (String uid : uids) {
            usersRef.child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null) {
                        if (names.length() > 0) names.append(", ");
                        names.append(name);
                        tvCollaborators.setText(names.toString());
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

    private void updateStatus(String newStatus) {
        taskRef.child("status").setValue(newStatus).addOnSuccessListener(aVoid -> 
            Toast.makeText(TaskDetailActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show()
        );
    }
}