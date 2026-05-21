package com.bissam.kanban;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CreateTaskDialog extends DialogFragment {

    private EditText etTitle, etDescription;
    private OnTaskSaveListener listener;

    public interface OnTaskSaveListener {
        void onSave(String title, String description);
    }

    public void setOnTaskSaveListener(OnTaskSaveListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_create_task, null);

        etTitle = view.findViewById(R.id.etTaskTitle);
        etDescription = view.findViewById(R.id.etTaskDescription);
        Button btnSave = view.findViewById(R.id.btnSaveTask);
        Button btnCancel = view.findViewById(R.id.btnCancelTask);

        builder.setView(view);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (!title.isEmpty()) {
                if (listener != null) {
                    listener.onSave(title, desc);
                }
                dismiss();
            } else {
                etTitle.setError("Title is required");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}