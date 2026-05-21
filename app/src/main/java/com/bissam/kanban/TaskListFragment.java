package com.bissam.kanban;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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

    private String status;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private EditText etSearch;
    private TaskAdapter adapter;
    private List<Task> allTasks = new ArrayList<>();
    private List<Task> filteredTasks = new ArrayList<>();
    private DatabaseReference db;
    private FirebaseAuth auth;

    public static TaskListFragment newInstance(String status) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        if (getArguments() != null) {
            status = getArguments().getString("status");
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearch);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TaskAdapter(filteredTasks, requireContext(), false);
        recyclerView.setAdapter(adapter);

        setupSearch();
        loadTasks();

        return view;
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        filteredTasks.clear();
        if (query.isEmpty()) {
            filteredTasks.addAll(allTasks);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Task task : allTasks) {
                if ((task.title != null && task.title.toLowerCase().contains(lowerQuery)) ||
                    (task.description != null && task.description.toLowerCase().contains(lowerQuery))) {
                    filteredTasks.add(task);
                }
            }
        }
        adapter.notifyDataSetChanged();
        checkEmpty();
    }

    private void checkEmpty() {
        if (filteredTasks.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadTasks() {
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
                filter(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}