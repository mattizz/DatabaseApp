package com.example.student.bazydanych;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.student.bazydanych.HelperDB;


public class Provider extends ContentProvider {

    private HelperDB helperDB;
    private SQLiteDatabase sqLiteDatabase;
    private static final String ID = "com.example.student.bazydanych";
    public static final Uri URI_CONTENT = Uri.parse("content://" + ID + "/" + HelperDB.NAME_TABLE);
    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ID,HelperDB.NAME_TABLE,WHOLE_TABLE);
        uriMatcher.addURI(ID,HelperDB.NAME_TABLE + "/#",SELECTED_ROW);
    }
    @Override
    public String getType(Uri uri) {return null;}

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        int typeUri = uriMatcher.match(uri);

        helperDB = new HelperDB(getContext());
        sqLiteDatabase = helperDB.getWritableDatabase();

        long idAdded = 0;
        switch (typeUri) {
            case WHOLE_TABLE:
                sqLiteDatabase.insert(HelperDB.NAME_TABLE,null,values);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(HelperDB.NAME_TABLE + "/" + idAdded);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typeUri = uriMatcher.match(uri);

        helperDB = new HelperDB(getContext());
        sqLiteDatabase = helperDB.getWritableDatabase();

        int numberOfDeleted = 0;
        switch (typeUri) {
            case WHOLE_TABLE:
                numberOfDeleted = sqLiteDatabase.delete(HelperDB.NAME_TABLE,selection,selectionArgs);
                break;

            case SELECTED_ROW:
                numberOfDeleted = sqLiteDatabase.delete(HelperDB.NAME_TABLE,addIdToSelection(selection,uri),selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numberOfDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typeUri = uriMatcher.match(uri);

        helperDB = new HelperDB(getContext());
        sqLiteDatabase = helperDB.getWritableDatabase();

        int numberUpdated = 0;

        switch (typeUri) {
            case WHOLE_TABLE:
                numberUpdated = sqLiteDatabase.update(HelperDB.NAME_TABLE,values,selection,selectionArgs);
                break;

            case SELECTED_ROW:
                numberUpdated = sqLiteDatabase.update(HelperDB.NAME_TABLE,values,addIdToSelection(selection,uri),selectionArgs);
                break;
            default:
                throw  new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numberUpdated;
    }

    @Override
    public boolean onCreate() {
        helperDB = new HelperDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int typeUri = uriMatcher.match(uri);

        helperDB = new HelperDB(getContext());
        sqLiteDatabase = helperDB.getWritableDatabase();

        Cursor cursor = null;

        switch (typeUri) {
            case WHOLE_TABLE:
                cursor = sqLiteDatabase.query(false,HelperDB.NAME_TABLE,projection,selection,selectionArgs,null,null,sortOrder,null,null);
                break;

            case SELECTED_ROW:
                cursor = sqLiteDatabase.query(false,HelperDB.NAME_TABLE,projection,addIdToSelection(selection,uri),selectionArgs,null,null,sortOrder,null,null);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    private String addIdToSelection(String selection,Uri uri) {
        if(selection != null && !selection.equals(""))
            selection = selection + " and " + HelperDB.ID + "=" + uri.getLastPathSegment();
        else
            selection = HelperDB.ID + "=" + uri.getLastPathSegment();
        return selection;
    }
}
