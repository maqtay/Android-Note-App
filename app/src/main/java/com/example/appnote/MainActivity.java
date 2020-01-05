package com.example.appnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView ls;
    ArrayAdapter adapter;
    ArrayList<Integer> idArray;
    ArrayList<String> titleArray;
    Intent i;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ls = findViewById(R.id.noteList);
        titleArray = new ArrayList<>();
        idArray = new ArrayList<>();
        i = new Intent(MainActivity.this, addNote.class);


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleArray);
        ls.setAdapter(adapter);

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                i.putExtra("noteId", idArray.get(position));
                i.putExtra("info", "old");
                startActivity(i);
            }
        });
        getData();

        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ls.setClickable(false);
                removeData(idArray.get(position));
                return false;
            }
        });
    }
    public void removeData(final int position1){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Not Silinecek!");
        alert.setMessage("Emin misiniz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    database = MainActivity.this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
                    database.execSQL("DELETE FROM notes WHERE id = ?", new String[] {String.valueOf(position1)});
                }catch (Exception e){
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                ls.setClickable(true);
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ls.setClickable(true);
            }
        });
        alert.show();

    }

    public void  getData(){
        try {
            database = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);
            Cursor c = database.rawQuery("SELECT * FROM notes", null);
            int titleIx = c.getColumnIndex("titleTxt");
            int idIx = c.getColumnIndex("id");
            while (c.moveToNext()){
                idArray.add(c.getInt(idIx));
                if (c.getString(titleIx).matches("")){
                    titleArray.add("(Başlıksız)");
                }else {
                    titleArray.add(c.getString(titleIx));
                }
            }
            adapter.notifyDataSetChanged();
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_exit:
                finish();
                break;
            case R.id.addNote:
                i.putExtra("info", "new");
                startActivity(i);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}