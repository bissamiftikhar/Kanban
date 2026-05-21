package com.bissam.kanban;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class KanbanApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyDuKBPQwvJsRXNk3Okp_jk3KwiPXQlNVlU")
                        .setApplicationId("1:965770812286:android:43a4eaf328a896205c712f")
                        .setProjectId("kanban-8d84d")
                        .setDatabaseUrl("https://kanban-8d84d-default-rtdb.firebaseio.com")
                        .setStorageBucket("kanban-8d84d.firebasestorage.app")
                        .setGcmSenderId("965770812286")
                        .build();
                FirebaseApp.initializeApp(this, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}