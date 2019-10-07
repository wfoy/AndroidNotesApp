package com.example.androidnotesapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;

public class NoteActivity extends AppCompatActivity {

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = getIntent().getStringExtra("Title");
        EditText editText = (EditText)findViewById(R.id.note_title);
        editText.setText(title, TextView.BufferType.EDITABLE);

        String word = getIntent().getStringExtra("Words");
        if (word != null) {
            EditText note = (EditText) findViewById(R.id.note_text);
            note.setText(word, TextView.BufferType.EDITABLE);
        }


        EditText note_word = (EditText)findViewById(R.id.note_text);
        note_word.requestFocus();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                EditText ttl = (EditText)findViewById(R.id.note_title);
                EditText note = (EditText)findViewById(R.id.note_text);
                data.putExtra("Title", ttl.getText().toString());
                data.putExtra("Rem", title);
                setResult(RESULT_OK, data);
                save(ttl.getText().toString(), note.getText().toString());
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** Writes textToSave to the file denoted by fileName. **/
    private void save(String fileName, String textToSave) {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(textToSave);
            out.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
