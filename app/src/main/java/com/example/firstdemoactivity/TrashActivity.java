package com.example.firstdemoactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrashActivity extends AppCompatActivity {

    private RecyclerView trashRecyclerView;
    private NoteAdapter adapter;
    private ArrayList<Note> allNotes;
    private Button emptyTrashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        Toolbar toolbar = findViewById(R.id.trash_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trashRecyclerView = findViewById(R.id.trashRecyclerView);
        emptyTrashButton = findViewById(R.id.btn_empty_trash);

        allNotes = NoteStorageHelper.loadNotes(this);

        adapter = new NoteAdapter(allNotes, true); // Enable trash filter
        adapter.filter("");

        trashRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trashRecyclerView.setAdapter(adapter);

        // Long click on trashed note
        adapter.setOnItemLongClickListener(position -> {
            Note selectedNote = adapter.getCurrentList().get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Trash Options")
                    .setMessage("What do you want to do with this note?")
                    .setPositiveButton("Restore", (dialog, which) -> {
                        selectedNote.setTrashed(false);
                        saveAndRefresh();
                        Toast.makeText(this, "Note restored", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Delete Permanently", (dialog, which) -> {
                        allNotes.remove(selectedNote);
                        saveAndRefresh();
                        Toast.makeText(this, "Note deleted permanently", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        });

        // Empty Trash Button
        emptyTrashButton.setOnClickListener(v -> {
            boolean hasTrash = false;
            for (Note note : allNotes) {
                if (note.isTrashed()) {
                    hasTrash = true;
                    break;
                }
            }

            if (!hasTrash) {
                Toast.makeText(this, "Trash is already empty", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Empty Trash")
                    .setMessage("Permanently delete all trashed notes?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        allNotes.removeIf(Note::isTrashed);
                        saveAndRefresh();
                        Toast.makeText(this, "Trash emptied", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }

    private void saveAndRefresh() {
        NoteStorageHelper.saveNotes(this, allNotes);
        adapter = new NoteAdapter(allNotes, true); // Set trash mode again
        trashRecyclerView.setAdapter(adapter);
        adapter.filter("");

        adapter.setOnItemLongClickListener(position -> {
            Note selectedNote = adapter.getCurrentList().get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Trash Options")
                    .setMessage("What do you want to do with this note?")
                    .setPositiveButton("Restore", (dialog, which) -> {
                        selectedNote.setTrashed(false);
                        saveAndRefresh();
                        Toast.makeText(this, "Note restored", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Delete Permanently", (dialog, which) -> {
                        allNotes.remove(selectedNote);
                        saveAndRefresh();
                        Toast.makeText(this, "Note deleted permanently", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
