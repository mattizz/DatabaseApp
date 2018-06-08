package com.example.student.bazydanych;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDB extends SQLiteOpenHelper
{
    public final static int VERSION_DATABASE = 1;
    public final static String ID = "_id";
    public final static String NAME_DATABASE = "MySQLite";
    public final static String NAME_TABLE = "Phone";
    public final static String COLUMN_PRODUCER = "Manufacturer";
    public final static String COLUMN_MODEL = "Model";
    public final static String COLUMN_VERSION = "Version";
    public final static String COLUMN_WWW = "WWW";
    public final static String CREATE_DATABASE = "CREATE TABLE " + NAME_TABLE + "("+ID+" integer primary key autoincrement, " +
            COLUMN_PRODUCER +" text not null," +
            COLUMN_MODEL +" text not null," +
            COLUMN_VERSION +" text not null," +
            COLUMN_WWW +" text not null);";
    private static final String DELETE_DATABASE = "DROP TABLE IF EXISTS " + NAME_TABLE;

    public HelperDB(Context context)
    {
        super(context,NAME_DATABASE,null,VERSION_DATABASE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {
    }
}
