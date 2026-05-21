package com.bissam.kanban;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    EditText etSearch;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        status = getArguments().getString("status");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        recyclerView = view.findViewById(R.id.recyclerView);
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskAdapter(allTasks, getContext(), false);
        recyclerView.setAdapter(adapter);

        loadTasks();
        setupSearch();

        return view;
    }

    void loadTasks() {
        String uid = auth.getCurrentUser().getUid();

        db.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allTasks.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task == null) continue;
                    if (!task.status.equals(status)) continue;

                    // show if owner or collaborator
                    boolean isOwner = uid.equals(task.ownerUid);
                    boolean isCollaborator = task.collaborators != null
                            && task.collaborators.containsKey(uid);

                    if (isOwner || isCollaborator) {
                        allTasks.add(task);
                    }
                }
                adapter.updateList(allTasks);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    void filterTasks(String query) {
        List<Task> filtered = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.title.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(task);
            }
        }
        adapter.updateList(filtered);
    }
}
