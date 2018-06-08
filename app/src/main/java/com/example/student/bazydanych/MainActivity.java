package com.example.student.bazydanych;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.navbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Telefony");

        listView = (ListView) findViewById(R.id.listItem);
        runLoader();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long option, boolean checked) {
                colorSelected();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.delete,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelected();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long option) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("option", option);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void runLoader() {
        getLoaderManager().initLoader(0, null, this);
        String[] mapFrom = new String[]{HelperDB.COLUMN_PRODUCER, HelperDB.COLUMN_MODEL};
        int[] mapTo = new int[]{R.id.Col1, R.id.Col2};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.row, null, mapFrom, mapTo, 0);
        listView.setAdapter(cursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, 0); //0 - dodaj nowy, 1-edycja
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {HelperDB.ID, HelperDB.COLUMN_PRODUCER, HelperDB.COLUMN_MODEL};
        CursorLoader cursorLoader = new CursorLoader(this, Provider.URI_CONTENT, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void deleteSelected() {
        long selected[] = listView.getCheckedItemIds();
        for (int i = 0; i < selected.length; ++i)
            getContentResolver().delete(ContentUris.withAppendedId(Provider.URI_CONTENT, selected[i]), null, null);
    }

    private void colorSelected() {
        for(int i=0; i< listView.getAdapter().getCount(); i++){
            listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }

        long selected[] = listView.getCheckedItemIds();
        for (int i = 0; i < listView.getAdapter().getCount(); ++i)
            for(int j=0; j< selected.length; j++){
                if(listView.getItemIdAtPosition(i)==selected[j]){
                    listView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                }
            }
    }
}