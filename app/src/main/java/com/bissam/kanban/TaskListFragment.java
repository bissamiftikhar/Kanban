package com.bissam.kanban;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    String status;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    List<Task> allTasks = new ArrayList<>();
    DatabaseReference db;
    FirebaseAuth auth;

    public static TaskListFragment newInstance(String status) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        if (getArguments() != null) {
            status = getArguments().getString("status");
        }
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TaskAdapter(allTasks, requireContext(), false);
        recyclerView.setAdapter(adapter);

        loadTasks();

        return view;
    }

    void loadTasks() {
        if (auth.getCurrentUser() == null) return;
        
        String uid = auth.getCurrentUser().getUid();
        db.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTasks.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task == null || task.status == null) continue;
                    if (!task.status.equals(status)) continue;

                    boolean isOwner = uid.equals(task.ownerUid);
                    boolean isCollaborator = task.collaborators != null && task.collaborators.containsKey(uid);

                    if (isOwner || isCollaborator) {
                        allTasks.add(task);
                    }
                }
                adapter.updateList(allTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}