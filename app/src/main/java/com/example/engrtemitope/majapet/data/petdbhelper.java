package com.example.engrtemitope.majapet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.engrtemitope.majapet.data.PetContract.PetEntry;
/**
 * Created by Engr. Temitope on 3/20/2018.
 */

public class petdbhelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = petdbhelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shelter.db";

    public petdbhelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }





    @Override
    public void onCreate(SQLiteDatabase db) {
       // private static final String TEXT_TYPE = "TEXT";
       // private static final String COMMA_SEP= ",";
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetEntry.TABLE_NAME + " (" + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PetEntry.COLUMN_PICTURE  + " TEXT NOT NULL, "
                + PetEntry.COLUMN_NAME  + " TEXT NOT NULL, "
                + PetEntry.COLUMN_BREED +  " TEXT, "
                + PetEntry.COLUMN_GENDER + " INTEGER NOT NULL, "
                + PetEntry.COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    //private static final String SQL_DELETE_ENTRIES= "DROP TABLE IF EXISTS" + PetEntry.TABLE_NAME;
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade(db,oldVersion,newVersion);
    }
}
