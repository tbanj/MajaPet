package com.example.engrtemitope.majapet;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.engrtemitope.majapet.data.PetContract.PetEntry;
import com.example.engrtemitope.majapet.data.PetCursorAdapter;
import com.example.engrtemitope.majapet.data.petdbhelper;

import static com.example.engrtemitope.majapet.MethodInUse.CursorPojection;
import static com.example.engrtemitope.majapet.data.PetContract.PetEntry.CONTENT_URI;

//import android.support.v7.widget.LinearLayoutManager

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER =0;
    PetCursorAdapter mCursorAdapter;

    private petdbhelper mDbHelper;
    private Toast mToastId;
    private static final String TAG = CatalogActivity.class.getName();
    private int newRowID;
    private TextView displayView;
    private TextView displayAll;
    private TextView dispAde;
    private int totalRow;
    private int countDummy;
    private ListView petListView;
    private ImageView petImage;

    MethodInUse methodInUse = new MethodInUse();

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    private String umm;
    private static final String LIFECYCLE_CALLBACKS_IMAGE_KEY = "callbacks";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new petdbhelper(this);
        petListView = findViewById(R.id.list);


        int viewL = (R.layout.list_item);
        View viewEmpty = findViewById(R.id.empty_view);
        petImage =findViewById(R.id.pet_icon);

        //Since we just initialize the Adapter the cursor shoudl be null
        mCursorAdapter= new PetCursorAdapter(this,null);
        petListView.setAdapter(mCursorAdapter);


        Log.v("petListView at 1", " "+ petListView.getChildCount());

        //setup item clickListener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent editorActivityIntent = new Intent(CatalogActivity.this,EditorActivity.class);

                //Cursor cursor =(Cursor) mCursorAdapter.getItem(position);
                Uri uri;
                uri= CONTENT_URI;
                //Log.v("CatalogActivity", "Oncreate() petListView CONTENT_URI:  "+CONTENT_URI);
                Log.v("CatalogActivity", "Oncreate() petListView ContentUri:  "+ ContentUris.withAppendedId(uri, id));

                //this is use to query the Pet data name which was clicked of the
                Uri currentPetUri =ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
//                um=currentPetUri;
                Cursor cursorClick = getContentResolver().query(currentPetUri, methodInUse.CursorPojection(), null, null, null);

                if(cursorClick.moveToFirst()){
                    do{
                        String startLocation = cursorClick.getString(1);
                        umm =startLocation;
                        String endLocation = cursorClick.getString(2);

                        Log.d("DB value", startLocation  );

                    }while(cursorClick.moveToNext());




                }

                cursorClick.close();
                editorActivityIntent.setData(currentPetUri);
                startActivity(editorActivityIntent);
            }
        });


        getLoaderManager().initLoader(URL_LOADER, null, this);
    }



    private ContentValues contentValuePetUpdate() {
        // Gets the database in write mode


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        // Uri image= "content://media/external/images/media/17995";
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PICTURE, R.drawable.pet_dummyn);
        values.put(PetEntry.COLUMN_NAME, "Milo");
        values.put(PetEntry.COLUMN_BREED, "bulldog");

        values.put(PetEntry.COLUMN_WEIGHT, 35);
        return values;
    }


    private ContentValues contentValuePet() {
        // Gets the database in write mode


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        //  values.put(PetEntry.COLUMN_PICTURE, R.drawable.pet_dummy);
        values.put(PetEntry.COLUMN_PICTURE, R.drawable.pet_dummy);
        values.put(PetEntry.COLUMN_NAME, "Toto");
        values.put(PetEntry.COLUMN_BREED, "Terrier");
        values.put(PetEntry.COLUMN_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_WEIGHT, 7);
        return values;
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */

    private void insertPet() {
        Log.v("EditorActivity", "Inputted mNamePet  : " + contentValuePet());
        getContentResolver().insert(CONTENT_URI,contentValuePet());


    }








        private  Cursor cursorDb() {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursorPet= db.query(PetEntry.TABLE_NAME,
                methodInUse.CursorPojection(),
                null,
                null,
                null,
                null,
                null);
        Log.v("CatalogActivity", "Cursor cursorPet: Total number of columns is : " + methodInUse.CursorPojection().length);

        //Log.v("CatalogActivity", "Cursor cursorPet: Total number of rows in the database is : " + cursor.getCount());

    return cursorPet;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
                        case R.id.action_insert_dummy_data:

                countDummy =countDummy + 1;


                insertPet();

                return true;

            case R.id.action_update_dummy_data:

                if(countDummy != 0) {
                    updatePet();
                    methodInUse.toastChangeL(CatalogActivity.this,
                            "Record Updated where Toto is the initial name");
                } else {
                    methodInUse.toastChangeL(CatalogActivity.this,
                            "No dummy data to be updated");

                }
                countDummy = 0;
                return true;
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deletePet();
                //displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePet() {

        SQLiteDatabase dbaUpdate = mDbHelper.getWritableDatabase();
        String selection = PetEntry.COLUMN_NAME + "=?";
        String[] selectionArgs = new String[] {"Toto"};
        getContentResolver().update(CONTENT_URI,contentValuePetUpdate(),selection,selectionArgs);

    }



    private void deletePet() {

        newRowID=getContentResolver().delete(CONTENT_URI,null,null);
        if (newRowID == -1) {
            mToastId.makeText(CatalogActivity.this, "Error deleting all Pets values : ",
                    mToastId.LENGTH_LONG).show();
        } else {

        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
         cursorDb();
        if (mListState != null)
            petListView.onRestoreInstanceState(mListState);
        mListState = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = petListView.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch(id) {
            case URL_LOADER:
                return  new CursorLoader(
                        this,
                        CONTENT_URI,
                        CursorPojection(),
                        null,
                        null,
                        null);

            default:
                return null;





        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback is called when data needs to be reset
        mCursorAdapter.swapCursor(null);

    }



}