package com.example.firstdemoactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button saveButton, colorButton;
    private EditText titleEdit, descEdit;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);

        toolbar = findViewById(R.id.toolbar_add_note);
        setSupportActionBar(toolbar);

        saveButton = findViewById(R.id.SaveButton);
        titleEdit = findViewById(R.id.titleEdit);
        descEdit = findViewById(R.id.descEdit);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // Save note
        saveButton.setOnClickListener(v -> {
            String title = titleEdit.getText().toString().trim();
            String description = descEdit.getText().toString().trim();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("note_title", title);
            resultIntent.putExtra("note_description", description);


            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
