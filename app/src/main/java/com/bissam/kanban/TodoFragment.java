package com.bissam.kanban;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TodoFragment extends Fragment {

    public TodoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void showCreateTaskDialog() {
        CreateTaskDialog dialog = new CreateTaskDialog();
        dialog.setOnTaskSaveListener((title, description) -> {
            saveTaskToFirebase(title, description);
        });
        dialog.show(getChildFragmentManager(), "CreateTaskDialog");
    }

    private void saveTaskToFirebase(String title, String description) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks");
        String taskId = ref.push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (user != null) ? user.getUid() : "unknown";
        String name = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Anonymous";

        Task newTask = new Task(taskId, title, description, "TODO", uid, name);

        if (taskId != null) {
            ref.child(taskId).setValue(newTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Task created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to create task", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}