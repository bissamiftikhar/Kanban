package com.bissam.kanban;

import java.util.HashMap;
import java.util.Map;

public class Task {
    public String taskId, title, description, status, ownerUid, ownerName;
    public Map<String, Boolean> collaborators;

    public Task() {}

    public Task(String taskId, String title, String description,
                String status, String ownerUid, String ownerName) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.ownerUid = ownerUid;
        this.ownerName = ownerName;
        this.collaborators = new HashMap<>();
    }
}