package com.example.firstdemoactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEdit, descEdit;
    private int notePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_note);
        setSupportActionBar(toolbar);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleEdit = findViewById(R.id.edit_title);
        descEdit = findViewById(R.id.edit_description);
        Button updateButton = findViewById(R.id.updateButton);

        // Get data from MainActivity
        Intent intent = getIntent();
        String title = intent.getStringExtra("note_title");
        String description = intent.getStringExtra("note_description");
        notePosition = intent.getIntExtra("note_position", -1);

        // Set to EditText
        titleEdit.setText(title);
        descEdit.setText(description);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTitle = titleEdit.getText().toString().trim();
                String updatedDescription = descEdit.getText().toString().trim();

                // 📅 Format and show update time
                long currentTime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                String formattedTime = sdf.format(new Date(currentTime));

                Toast.makeText(EditNoteActivity.this, "Note updated at " + formattedTime, Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("note_title", updatedTitle);
                resultIntent.putExtra("note_description", updatedDescription);
                resultIntent.putExtra("note_position", notePosition);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
