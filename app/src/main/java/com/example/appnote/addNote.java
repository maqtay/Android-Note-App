package com.example.appnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class addNote extends AppCompatActivity {
    EditText titleText, explainText;
    SQLiteDatabase database;
    String oldTitle, oldExp, currentTitle, currentExp, info;
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        titleText = findViewById(R.id.titleText);
        explainText = findViewById(R.id.explainText);


        database = openOrCreateDatabase("Notes", MODE_PRIVATE, null);
        Intent i = getIntent();
        info = i.getStringExtra("info");
        noteId = i.getIntExtra("noteId", 1);
        if (info.matches("new")) {
            titleText.setText("");
            explainText.setText("");
        } else {
            try {
                Cursor c = database.rawQuery("SELECT * FROM notes WHERE id = ?", new String[]{String.valueOf(noteId)});
                int titleIx = c.getColumnIndex("titleTxt");
                int expIx = c.getColumnIndex("explainTxt");
                while (c.moveToNext()) {
                    oldTitle = c.getString(titleIx);
                    oldExp = c.getString(expIx);
                    titleText.setText(oldTitle);
                    explainText.setText(oldExp);

                }
                c.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }

    }

    public String isChanged(String oldTitle, String oldExp, int noteId) {
        currentExp = explainText.getText().toString();
        currentTitle = titleText.getText().toString();
        if (currentTitle.matches("") && currentExp.matches("")){
            return "rmv";
        }else if (!(oldTitle.matches(currentTitle) && oldExp.matches(currentExp))) {
            try {
                database.execSQL("UPDATE notes SET titleTxt = ?, explainTxt = ? where id = ? ",
                        new String[]{currentTitle, currentExp, String.valueOf(noteId)});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "save";
        }else{
            return "dont";
        }
    }
    public void removeData(int noteId1) {
        try {
            database = openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            database.execSQL("DELETE FROM notes WHERE id = ?", new String[]{String.valueOf(noteId1)});
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        goBack();

    }

    public void save() {
        if (info.matches("new")) {
            String title = titleText.getText().toString();
            String explain = explainText.getText().toString();
            try {
                database = openOrCreateDatabase("Notes", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS notes(id INTEGER PRIMARY KEY ," +
                        "titleTxt VARCHAR(30), explainTxt VARCHAR(1000), time DATE)");
                String sqlString = "INSERT INTO notes (titleTxt, explainTxt, time) VALUES(?,?,?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, title);
                sqLiteStatement.bindString(2, explain);
                sqLiteStatement.execute();
                Toast.makeText(this, "Kaydedildi.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else if (info.matches("old") && isChanged(oldTitle, oldExp, noteId).matches("save") && !titleText.getText().toString().matches("")) {
            Toast.makeText(addNote.this, "Değişiklikler Kaydedildi!", Toast.LENGTH_SHORT).show();
        } else if(info.matches("old") && isChanged(oldTitle,oldExp,noteId).matches("rmv")){
            removeData(noteId);
        }
        goBack();
    }

    @Override
    public void onBackPressed() {
        if (info.matches("old") && isChanged(oldTitle, oldExp, noteId).matches("save")){
            Toast.makeText(addNote.this, "Değişiklikler kaydedildi", Toast.LENGTH_SHORT).show();
        }else if (info.matches("old") && isChanged(oldTitle,oldExp,noteId).matches("rmv")){
            removeData(noteId);
        }
        goBack();
    }

    public void goBack() {
        Intent i = new Intent(addNote.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.saveBtn) {
            save();
        } else if (item.getItemId() == R.id.deleteNote) {
            removeData(noteId);
        }
        return super.onOptionsItemSelected(item);
    }
}