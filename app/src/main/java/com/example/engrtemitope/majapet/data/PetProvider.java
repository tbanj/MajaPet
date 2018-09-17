package com.example.engrtemitope.majapet.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.engrtemitope.majapet.MethodInUse;
import com.example.engrtemitope.majapet.data.PetContract.PetEntry;

import static com.example.engrtemitope.majapet.data.PetContract.PetEntry.CONTENT_AUTHORITY;
import static com.example.engrtemitope.majapet.data.PetContract.PetEntry.PATH_PETS;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    //private Long id;
    private Toast mToastId;

    /* Database Helper Object*/
    private com.example.engrtemitope.majapet.data.petdbhelper mDBHelper;
    private int mGender = 0;

    /*Tag for the log message*/
    public static final String LOG_TAG =PetProvider.class.getSimpleName();

    //CatalogActivity catalogActivity= new CatalogActivity();

    private static final int PETS =100;

    private static final int PET_ID =101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS, PETS);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS +"/#", PET_ID);


    }


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        /* Create a global variable which will be accessible by different class in the project*/
        mDBHelper = new petdbhelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor =null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                // could contain multiple rows of the pets table


                cursor = database.query(PetEntry.TABLE_NAME,
                        CursorPojection(),
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case PET_ID:

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //this is use to automatically load the cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        Log.v("PetProvider", "query() Uri:  "+uri);
        return  cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType( Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        //use to debug the value which is printed out by UriMatcher
        Log.v("PetProvider", "insert():  "+ String.valueOf(match));


        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        //Toast mToastId=null;
        // TODO: Insert a new pet into the pets database table with the given ContentValues

        // Check that the name is not null
        String nPicture =values.getAsString(PetEntry.COLUMN_PICTURE);
        if (nPicture == null ) {
            throw new IllegalArgumentException("Pet requires a picture");
        }

        // Check that the name is not null
        String nNameP =values.getAsString(PetEntry.COLUMN_NAME);
        if (nNameP == null|| nNameP.length()<2 ) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        String nBreedP =values.getAsString(PetEntry.COLUMN_BREED);
        if (nBreedP == null|| nBreedP.length()<2) {
            throw new IllegalArgumentException("Pet requires a breed");
        }

        // Check that the gender is valid
        Integer nGenderP =values.getAsInteger(PetEntry.COLUMN_GENDER);
        if (nGenderP == null || !PetEntry.isValidGender(nGenderP)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer nWeightP =values.getAsInteger(PetEntry.COLUMN_WEIGHT);
        try{
            if (nWeightP  != null && nWeightP  < 0) {
                Log.v("PetProvider", "insertPet : nWeightP: "+nWeightP);
                throw new IllegalArgumentException("Pet requires valid weight");

            }
        }catch
            (Exception err){
                    //emptyToast.makeText(MainActivity.this,"No data submitted",Toast.LENGTH_SHORT).show();
            MethodInUse methodInUse= new MethodInUse();
            Log.v("You inputted:", "wrong Weight Value ");


        }



        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id= db.insert(PetEntry.TABLE_NAME,null,values);
        //Log.v("PetProvider", "List of Columns makes use of are:  ");
        if (id == -1) {
            mToastId.makeText(getContext(), "Error with saving Pet : ",
                    mToastId.LENGTH_LONG).show();
        } else {
            mToastId.makeText(getContext(), "Pet save with ID: " + String.valueOf(id),
                    mToastId.LENGTH_LONG).show();
        }

        //Notify all listeners that the data has changedfor the Pet content URI
        getContext().getContentResolver().notifyChange(uri,null);

        db.close();

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        Log.v("PetProvider", "Insert() ContentUri:  "+ContentUris.withAppendedId(uri, id));
        return ContentUris.withAppendedId(uri, id);


    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        Log.v("PetProvider", "Delete() Uri:  "+uri);
        int totalRowDelete = 0;
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                totalRowDelete = database.delete(PetEntry.TABLE_NAME,null,null );
               mToastId.makeText(getContext(), "Total number of Pet deleted is: " + totalRowDelete,
                       mToastId.LENGTH_LONG).show();
                //return totalRowDelete;
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //return database.delete(uri,selection,selectionArgs);
                totalRowDelete = database.delete(PetEntry.TABLE_NAME,selection,selectionArgs );
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        database.close();
        return totalRowDelete;
    }





    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        Log.v("PetProvider", "Update() Uri:  "+uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //updatePet(ur);

                return updatePet(uri,values,selection,selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }






    }

    private int updatePet(Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        // TODO: Update the selected pets in the pets database table with the given ContentValues
        if (values.containsKey(PetEntry.COLUMN_PICTURE)) {
            String petPicture = values.getAsString(PetEntry.COLUMN_PICTURE);
            if (petPicture == null) {
                throw new IllegalArgumentException("Pet requires a picture");
            }
        }

        if (values.containsKey(PetEntry.COLUMN_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // If there are no values to update, then don't try to update the database

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri,null);

        // TODO: Return the number of rows that were affected
        // Returns the number of database rows affected by the update statement
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);



    }
    private String[] CursorPojection() {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PICTURE,
                PetEntry.COLUMN_NAME,
                PetEntry.COLUMN_BREED,
                PetEntry.COLUMN_GENDER,
                PetEntry.COLUMN_WEIGHT};
        return projection;
    }
}
