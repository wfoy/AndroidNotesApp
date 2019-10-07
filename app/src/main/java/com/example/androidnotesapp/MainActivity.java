package com.example.androidnotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;
    private final ArrayList<String> notes = new ArrayList<String>();
    ArrayAdapter<String> itemsAdapter;
    String last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if notes already exist
        if(fileExists("__NOTES__")) {
            String[] entries = open("__NOTES__").split(" ", 0);
            for(int j = 0; j < entries.length; j++)
            {
                if(entries[j] != null && !entries[j].isEmpty() && !entries[j].equals("") && !entries[j].equals(" ")) {
                    notes.add(entries[j]);
                }
            }
            //deal with extra entry appearing
            if(notes.size() > 0) {
                if (!notes.get(notes.size() - 1).equals(last)) {
                    notes.remove(notes.size() - 1);
                }
            }
        }

        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,notes);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(itemsAdapter);

        final int REQUEST_CODE = 20;
        FloatingActionButton button = findViewById(R.id.button); //add note
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("Title", "Note" + Integer.toString(notes.size()));
                MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });

        listView.setClickable(true);//click on note
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String name = listView.getItemAtPosition(position).toString();
                if(fileExists(name)) {
                    Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                    intent.putExtra("Title", name);
                    intent.putExtra("Words", open(name));
                    MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        FloatingActionButton delbutton = findViewById(R.id.d_button); //delete last note
        delbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(notes.size() > 0) {
                    notes.remove(notes.size() - 1);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String name = data.getExtras().getString("Title");
            String prev = data.getExtras().getString("Rem");

            if(!name.isEmpty() && name != null && !notes.contains(name) && !name.equals("") && !name.equals(" ")) {
                int i = notes.indexOf(prev);
                if(i != -1)
                    notes.set(i, name);//update note title
                else
                    notes.add(name);//add note title if new
            }
            itemsAdapter.notifyDataSetChanged();
        }
    }

    /** Checks if the file denoted by fileName exists. **/
    private boolean fileExists(String fileName) {
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    /** Opens the file denoted by fileName and returns the contents of the file. **/
    private String open(String fileName) {
        String content = "";
        if (fileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //create string of titles of notes
        String toSave = "";
        for(int i = 0; i < notes.size(); i++)
        {
            toSave += notes.get(i) + " ";
        }

        save("__NOTES__", toSave);//save string of titles to __NOTES__ file
        if(notes.size() > 0)
            last = notes.get(notes.size()-1);
    }

}
