package com.example.student.bazydanych;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private EditText editProducer;
    private EditText editModel;
    private EditText editVersion;
    private EditText editWWW;

    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonWWW;

    private boolean producerIsOk;
    private boolean modelIsOk;
    private boolean androidVersionIsOk;
    private boolean wwwIsOk;

    private long option=0; //0-dodawanie telefonu   inny-edycja telefonu

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //TOOLBAR
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.navbar2);
        setSupportActionBar(toolbar);

        editProducer = (EditText) findViewById(R.id.editTextProducer);
        editModel = (EditText) findViewById(R.id.editTextModel);
        editVersion = (EditText) findViewById(R.id.editTextVersion);
        editWWW = (EditText) findViewById(R.id.editTextWWW);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonWWW = (Button) findViewById(R.id.buttonWWW);

        saveOrEdit();

        if(option != 0){ //edycja telefonu, ustaw pola do edycji
            getSupportActionBar().setTitle("Edycja");
            setFields();
        }else{
            getSupportActionBar().setTitle("Dodaj telefon");
        }

        validate();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(producerIsOk && modelIsOk && androidVersionIsOk && wwwIsOk) {
                    ContentValues values = new ContentValues();
                    values.put(HelperDB.COLUMN_PRODUCER, editProducer.getText().toString());
                    values.put(HelperDB.COLUMN_MODEL, editModel.getText().toString());
                    values.put(HelperDB.COLUMN_VERSION, editVersion.getText().toString());
                    values.put(HelperDB.COLUMN_WWW, editWWW.getText().toString());

                    if(option == 0){
                        getContentResolver().insert(Provider.URI_CONTENT, values);
                    }else {
                        getContentResolver().update(ContentUris.withAppendedId(Provider.URI_CONTENT, option),values,null,null);
                    }

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Podane dane sa nieprawidlowe",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonWWW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editWWW.getText().toString().isEmpty()){
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(editWWW.getText().toString()));
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Brak strony WWW",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validate() {
        editProducer.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                producerIsOk = true;

                if(editProducer.getText().toString().isEmpty()) {
                    editProducer.setError("Pole nie może być puste!");
                    producerIsOk = false;
                }

                else if(editProducer.getText().toString().matches(".*\\s+.*")) {
                    editProducer.setError("Pole nie możę zawierać spacji");
                    producerIsOk = false;
                }
            }
        });

        editModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modelIsOk = true;

                if(editModel.getText().toString().isEmpty()) {
                    editModel.setError("Pole nie może być puste!");
                    modelIsOk = false;
                }
            }
        });

        editVersion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                androidVersionIsOk = true;

                if(editVersion.getText().toString().isEmpty()) {
                    editVersion.setError("Pole nie może być puste!");
                    androidVersionIsOk = false;
                }
            }
        });

        editWWW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wwwIsOk=true;

                if(editWWW.getText().toString().isEmpty()) {
                    editWWW.setError("Pole nie może być puste!");
                    wwwIsOk = false;
                } else if(!(editWWW.getText().toString().startsWith("http://") || editWWW.getText().toString().startsWith("https://"))) {
                    editWWW.setError("Adres musi zawierać http:// lub https://");
                    wwwIsOk=false;
                }
            }
        });
    }

    private void saveOrEdit() {
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
              option = bundle.getLong("option");
        }
    }

    private void setFields() {
        String projection[] = {HelperDB.COLUMN_PRODUCER, HelperDB.COLUMN_MODEL, HelperDB.COLUMN_VERSION, HelperDB.COLUMN_WWW};
        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(Provider.URI_CONTENT,option),projection,null,null,null);
        cursor.moveToFirst();
        int indexOfColumn = cursor.getColumnIndexOrThrow(HelperDB.COLUMN_PRODUCER);
        String value = cursor.getString(indexOfColumn);
        editProducer.setText(value);
        editModel.setText(cursor.getString(cursor.getColumnIndexOrThrow(HelperDB.COLUMN_MODEL)));
        editVersion.setText(cursor.getString(cursor.getColumnIndexOrThrow(HelperDB.COLUMN_VERSION)));
        editWWW.setText(cursor.getString(cursor.getColumnIndexOrThrow(HelperDB.COLUMN_WWW)));
        producerIsOk = true;
        modelIsOk = true;
        androidVersionIsOk = true;
        wwwIsOk = true;
        cursor.close();
    }

    //Zapis danych przy obrocie
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("option",option);
        outState.putBoolean("Producer",producerIsOk);
        outState.putBoolean("Model",modelIsOk);
        outState.putBoolean("Version",androidVersionIsOk);
        outState.putBoolean("WWW",wwwIsOk);
        super.onSaveInstanceState(outState);
    }

    //Odczyt danych przy obrocie
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        option = savedInstanceState.getLong("option");
        producerIsOk = savedInstanceState.getBoolean("Producer");
        modelIsOk = savedInstanceState.getBoolean("Model");
        androidVersionIsOk = savedInstanceState.getBoolean("Version");
        wwwIsOk = savedInstanceState.getBoolean("WWW");

        if(option != 0){
            setFields();
        }
    }
}
