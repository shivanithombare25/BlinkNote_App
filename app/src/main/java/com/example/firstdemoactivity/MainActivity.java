package com.example.firstdemoactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private ArrayList<Note> noteList;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notepad");

        // Load notes
        recyclerView = findViewById(R.id.recyclerView);
        noteList = NoteStorageHelper.loadNotes(this);

        ArrayList<Note> filteredNotes = new ArrayList<>();
        for (Note note : noteList) {
            if (!note.isTrashed()) {
                filteredNotes.add(note);
            }
        }
        noteList = filteredNotes;


        // Sort notes by latest updated time
        Collections.sort(noteList, (n1, n2) -> Long.compare(n2.getUpdatedTime(), n1.getUpdatedTime()));

        adapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Click: edit note
        adapter.setOnItemClickListener((note, position) -> {
            int actualPosition = adapter.getFullList().indexOf(note);
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("note_title", note.getTitle());
            intent.putExtra("note_description", note.getDescription());
            intent.putExtra("note_position", actualPosition);
            startActivityForResult(intent, 2);
        });

        // Long Click: delete note (move to trash)
        adapter.setOnItemLongClickListener(position -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Note noteToDelete = adapter.getCurrentList().get(position);
                        noteToDelete.setTrashed(true);
                        adapter.filter("");
                        NoteStorageHelper.saveNotes(MainActivity.this, adapter.getFullList());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // FAB: Add note
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search notes...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_trash) {
            startActivity(new Intent(MainActivity.this, TrashActivity.class));
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Sort Dialog for 4 options
    private void showSortDialog() {
        String[] options = {"Newest First", "Oldest First", "Title A-Z", "Title Z-A"};

        new AlertDialog.Builder(this)
                .setTitle("Sort Notes")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Collections.sort(adapter.getFullList(), (n1, n2) -> Long.compare(n2.getUpdatedTime(), n1.getUpdatedTime()));
                            break;
                        case 1:
                            Collections.sort(adapter.getFullList(), (n1, n2) -> Long.compare(n1.getUpdatedTime(), n2.getUpdatedTime()));
                            break;
                        case 2:
                            Collections.sort(adapter.getFullList(), (n1, n2) -> n1.getTitle().compareToIgnoreCase(n2.getTitle()));
                            break;
                        case 3:
                            Collections.sort(adapter.getFullList(), (n1, n2) -> n2.getTitle().compareToIgnoreCase(n1.getTitle()));
                            break;
                    }
                    adapter.filter(""); // Refresh list
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String title = data.getStringExtra("note_title");
            String description = data.getStringExtra("note_description");

            Note newNote = new Note(title, description);
            adapter.getFullList().add(newNote);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String updatedTitle = data.getStringExtra("note_title");
            String updatedDescription = data.getStringExtra("note_description");
            int position = data.getIntExtra("note_position", -1);

            if (position != -1) {
                Note note = adapter.getFullList().get(position);
                note.setTitle(updatedTitle);
                note.setDescription(updatedDescription);
            }
        }

        // Re-sort after add/edit
        Collections.sort(adapter.getFullList(), (n1, n2) -> Long.compare(n2.getUpdatedTime(), n1.getUpdatedTime()));
        adapter.filter("");
        NoteStorageHelper.saveNotes(this, adapter.getFullList());
    }
}
