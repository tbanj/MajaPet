/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.engrtemitope.majapet;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.engrtemitope.majapet.data.PetContract;
import com.example.engrtemitope.majapet.data.PetContract.PetEntry;
import com.example.engrtemitope.majapet.data.petdbhelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.engrtemitope.majapet.MethodInUse.CursorPojection;

//importing the whole fole directory so as to make use of extension only

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity  {

    private static final String TAG = EditorActivity.class.getName();
    private TextView ade;

    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";

    private String dem;
    private static final String ON_CREATE = "onCreate";
    private static final String ON_START = "onStart";
    private static final String ON_RESUME = "onResume";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_STOP = "onStop";
    private static final String ON_RESTART = "onRestart";
    private static final String ON_DESTROY = "onDestroy";
    private static final String ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";


    // The URI of photo taken with camera
    private Uri uriPhoto;
    private Uri imageUri;
    private Uri fileUri;
    private Bitmap bitmap;
    // Storage for camera image URI componentsprivate final static String

    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";

    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";


// Required for camera operations in order to save the image file on resume.

    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;

    private int mIntNamePicture;
    private  String mNamePicture;

    private Uri currentPetUri;

    MethodInUse methodInUse = new MethodInUse();
    private int petID;

    private petdbhelper mDbHelper;


    private EditText mNameEditText;


    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    private Spinner mGenderSpinner;
/********List of EditText Above is use to Input fresh Pet's data to the database***********/

    // image of the pet
    private ImageView bFromGallery;
    private ImageView bSetImageCenter;
    // Flag to indicate the request of the next task to be perf
    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final String TEMP_IMAGE_NAME = "tempImage";

    //

    /*Tag for the log message*/
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /*For Error Message for Pet Name, Breed & Weight of Pet when its not properly inputted*/
    String mPetT = "pet picture ";
    String mNameT = "pet name ";
    String mWeightT = "pet weight number ";
    String mBreedT = "breed ";



    private int mGender = 0;


    //to restore image and camera uri after the image has been rotated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {

            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(mCurrentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            checkExif(exif);


        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            imageUri= null;
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
            bSetImageCenter.setVisibility(View.GONE);
            bFromGallery.setImageURI(mCapturedImageURI);}

        super.onRestoreInstanceState(savedInstanceState);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                methodInUse.CheckingEditText(mNameEditText, mNameT, 3, hasFocus);

            }
        });
        mBreedEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                methodInUse.CheckingEditText(mBreedEditText, mBreedT, 3, hasFocus);
            }
        });
        mWeightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                methodInUse.CheckingEditText(mWeightEditText, mWeightT, 1, hasFocus);
            }
        });

        setupSpinner();
        bSetImageCenter = findViewById(R.id.image_center);
        bFromGallery = findViewById(R.id.image_base);

        bFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //selectImageInGallery();
                Intent chooseImageIntent = getPickImageIntent(EditorActivity.this);
                startActivityForResult(chooseImageIntent, REQUEST_SELECT_IMAGE);

            }
        });


        Intent intent=  getIntent();
        currentPetUri =intent.getData();
        //String endLocation= intent.getExtras();
        if(currentPetUri==null){
            setTitle("Create a Pet");
        }
        else{

            setTitle(getString(R.string.editor_activity_title_new_pet));
            //currentPetUri;
            Log.v("EditorActivity", "List of all Pet data : "+"\n"+ currentPetUri);

            Cursor cursorClick = getContentResolver().query(currentPetUri, methodInUse.CursorPojection(),
                    null, null, null);

            if(cursorClick.moveToFirst()){
                do{
                    String petNameID = cursorClick.getString(0);
                    petID = Integer.parseInt(petNameID);
//                    String petPictureEdit = cursorClick.getString(1);

                    String pet_icon = (cursorClick.getString(cursorClick.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PICTURE)));
                    dem = pet_icon;
                    Log.v("EditorActivity","Error encounter while populating data : "+ petNameID);
                    String petNameEdit = cursorClick.getString(2);
                    String petBreedEdit = cursorClick.getString(3);
                    String petGenderEdit = cursorClick.getString(4);
                    String petWeightEdit = cursorClick.getString(5);

                    int mPetWeightEdit = Integer.parseInt(petWeightEdit);


                    mNameEditText.setText(petNameEdit);
                    mBreedEditText.setText(petBreedEdit);
                    mGenderSpinner.setSelection(Integer.parseInt(petGenderEdit));
                    mWeightEditText.setText(petWeightEdit);
                    try {
                        if(pet_icon.startsWith("content")|| pet_icon.startsWith("file")) {
                            imageUri= Uri.parse(pet_icon);

                            if(pet_icon.startsWith("content")){
                                bFromGallery.setImageURI(imageUri);
                                bSetImageCenter.setVisibility(View.GONE);




                            }

                            else if(pet_icon.startsWith("file")){
                                fileUri= Uri.parse(pet_icon);

                                String imageRotateCheck= fileUri.getPath();

                                // is use to check if the picture is distorted
                                ExifInterface exif = new ExifInterface(imageRotateCheck);

                                checkExif(exif);


                            bFromGallery.setVisibility(View.VISIBLE);
                                bSetImageCenter.setVisibility(View.GONE);
                                bFromGallery.setImageURI(fileUri);
                                // imageUri= fileUri;

                            }



//                            bFromGallery.setVisibility(View.VISIBLE);



                        }
                        else if(Integer.parseInt(pet_icon) == 2131230836 || Integer.parseInt(pet_icon) == 2131230835){
                            bFromGallery.setImageResource(Integer.parseInt(pet_icon));
                            mIntNamePicture = Integer.parseInt(pet_icon);
                            bFromGallery.setVisibility(View.VISIBLE);
                            bSetImageCenter.setVisibility(View.GONE);
                        }




                    }catch (Exception err){
                        Log.v("EditorActivity","Error encounter while populating data : ");
                    }

                    Log.d("EditorActivity", "DB value "+mIntNamePicture+ " "
                            +petNameEdit +" "+ petBreedEdit+ " "+ petGenderEdit+" "+petWeightEdit);
                }while(cursorClick.moveToNext());


            }
            cursorClick.close();



        }





        mDbHelper = new petdbhelper(this);
        //catalogActivity.Nm();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (mCurrentPhotoPath != null) {

            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
            mCapturedImageURI = null;
            mCurrentPhotoPath = null;
        }
        if (mCapturedImageURI != null) {

            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());

        }

        super.onSaveInstanceState(savedInstanceState);
    }

    // Deal with the result of selection of the photos and faces.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE:

                try {
                    if (resultCode == RESULT_OK) {

                        if (data == null || data.getData() == null) {
                            imageUri = uriPhoto;
                            mCurrentPhotoPath= imageUri.getPath();
                            // is use to check if the picture is distorted
                            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);

                            checkExif(exif);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            String imageRotateCheck= imageUri.getPath();

                            Log.v(TAG, " onActivityResult():u " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1));
                            // mNamePicture =mCurrentPhotoPath;
                        } else {
                            imageUri = data.getData();
                            mCapturedImageURI = imageUri;
                            bSetImageCenter.setVisibility(View.GONE);
                            bFromGallery.setRotation(0);
                            bFromGallery.setImageURI(mCapturedImageURI);




                        }
                        mNamePicture= imageUri.toString();

                    }
                } catch (Exception error) {
                    Log.d(TAG, " onActivityResult(): " + " Select Image= 1  Camera=0: ");
                    Log.d(TAG, " onActivityResult(): " + " Image upload type that is selected is: " + requestCode);

                }

                break;
            default:
                break;
        }
    }


    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    private void insertPet() {
        //mNameEditText =(EditText) findViewById(R.i.edit_pet_name);
        String mNamePet, mNameBreed, mNameWeight;
        int mIntNameWeight;



        mNamePet = mNameEditText.getText().toString().trim();

        Log.v("EditorActivity", "Inputted mNamePet  : " + mNamePet);

        mNameBreed = mBreedEditText.getText().toString().trim();

        Log.v("EditorActivity", "Inputted mNameBread  : " + mNameBreed);


        mNameWeight = mWeightEditText.getText().toString().trim();

        Log.v("EditorActivity", "Inputted mNameWeight  : " + mNameWeight);

        mIntNameWeight = Integer.parseInt(mNameWeight);
        //emptyTextEdit(mWeightEditText, "Pet Name Inputted");
        Log.v("EditorActivity", "Inputted mIntNameWeight : " + mIntNameWeight);

        ContentValues values = new ContentValues();
        if(mNamePicture == "2131230836" || mNamePicture == "2131230835") {

            values.put(PetEntry.COLUMN_PICTURE, mNamePicture);
        }
        else if(mNamePicture.startsWith("android")) {
            values.put(PetEntry.COLUMN_PICTURE, mNamePicture);
        }

        else if(mNamePicture.startsWith("file")) {
            String uriPhotoCheck =imageUri.getPath();
//            String petAddresshh= uriPhotoCheck.substring(uriPhotoCheck.lastIndexOf("//"+ 1));
            Log.v(TAG, " onActivityResult():u " + uriPhotoCheck);
            Uri imageUroo =Uri.fromFile(new File(String.valueOf(uriPhotoCheck)));
            Log.v(TAG, " onActivityResult():u " + imageUroo);
            values.put(PetEntry.COLUMN_PICTURE, imageUroo.toString());
        }
        else if(mNamePicture.startsWith("con")) {
            values.put(PetEntry.COLUMN_PICTURE, mNamePicture);
        }

        values.put(PetEntry.COLUMN_NAME, mNamePet);
        values.put(PetEntry.COLUMN_BREED, mNameBreed);
        values.put(PetEntry.COLUMN_GENDER, mGender);
        values.put(PetEntry.COLUMN_WEIGHT, mIntNameWeight);

        Uri uriNewId = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu

        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option

            case R.id.action_save:
                // It save Pet Data to Database

                //is use to check if data is properly inputted
                try {
                    if (validate() == true) {
                        insertPet();
                        methodInUse.toastChangeL(EditorActivity.this, "data submitted");
                        finish();
                    } else {
                        methodInUse.toastChangeL(EditorActivity.this, "No data submitted due to incomplete data inputted");
                    }
                } catch (Exception err) {
                    methodInUse.toastChangeL(EditorActivity.this, "No data submitted");

                }


                return true;
            case R.id.action_update:
                //Below code will update data of Pets which was stored in the database

                if ( validateUpdateId() ==true && validate() == true ) {
                    updateInsert();
                    methodInUse.toastChangeL(EditorActivity.this, "data records updated");


                    finish();
                } else {

                    methodInUse.toastChangeL(EditorActivity.this, "Existing data was not updated due to incomplete data inputted");
                  }
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                deletePet();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePet() {

        //to display or setText into a textView which is located in different layout you need to
        //set the contentView of the resourcce file
        int newRowI;
        int totalCountRow= queryAllColumns();
        TextView fIdText =findViewById(R.id.edit_pet_name);
        int fId =fIdText.getInputType();
        try {
            if(TextUtils.isEmpty(mNameEditText.getText().toString().trim())){
                totalCountRow=getContentResolver().delete(PetEntry.CONTENT_URI,null,null);
                Log.v("EditorActivity","totalDataRow "+String.valueOf(totalCountRow));
                if (totalCountRow == -1) {
                    methodInUse.toastChangeL(EditorActivity.this,
                            "Error deleting all Pets values :");


                } else {}


            }

            else{
                String selection = PetEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(PetEntry._ID)};

                Uri mn= ContentUris.withAppendedId(PetEntry.CONTENT_URI,Long.parseLong(fIdText.getText().toString()));
                totalCountRow=getContentResolver().delete(mn,selection,selectionArgs);
                Log.v("EditorActivity","Pet Save with id: "+ String.valueOf(totalCountRow));
                if (totalCountRow == -1) {
                    methodInUse.toastChangeL(EditorActivity.this,"Error deleting all Pets values :");


                } else {}
            }

            }
        catch (NumberFormatException e) {
            Log.i("EditorActivity",mNameEditText+" is not a number");
        }
        //Log.v("EditorActivity","TextView  catalogActivity.dispTotalRow  "+String.valueOf(totalCountRow));
        methodInUse.toastChangeL(EditorActivity.this,"Total Pet rows deleted: " + totalCountRow);

    }

    private int queryAllColumns() {
        //mDbHelper = new petdbhelper(this);
        //SQLiteDatabase dbUpdate = mDbHelper.getReadableDatabase();


        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, methodInUse.CursorPojection(), null, null, null);
        int totalDataRow=cursor.getCount();

        cursor.close();

        Log.v("EditorActivity","queryAllColumns()"+ CursorPojection()[0]+CursorPojection()[0]);
        Log.v("EditorActivity","totalDataRow "+String.valueOf(totalDataRow));
        return totalDataRow;

    }


    private String[] dataQureyNameColumn() {
        String[] columnNameOnly = new String[]{PetEntry.COLUMN_NAME};
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, columnNameOnly, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME);
        String namePet = null;
        String[] namePetArray ;
        List<String> cour1 = new ArrayList<String>();
        if (cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {

                namePet = cursor.getString(nameColumnIndex);

                cour1.add(namePet);

            }
        } else {

            cursor.moveToFirst();
        }
        //convert from ArrayList to Array
        namePetArray =cour1.toArray(new String[0]);
        cursor.close();
        return namePetArray;

    }


    private void updateInsert() {



        String nNamePet, nNameBreed, nNameWeight, mNamePicture;
        int nIntNameWeight;
        mDbHelper = new petdbhelper(this);

        nNamePet = mNameEditText.getText().toString().trim();
//
        Log.v("EditorActivity", "Inputted mNamePet  : " + nNamePet);

        nNameBreed = mBreedEditText.getText().toString().trim();

        Log.v("EditorActivity", "Inputted mNameBread  : " + nNameBreed);


        nNameWeight = mWeightEditText.getText().toString().trim();

        Log.v("EditorActivity", "Inputted mNameWeight  : " + nNameWeight);

        nIntNameWeight = Integer.parseInt(nNameWeight);


        //emptyTextEdit(mWeightEditText, "Pet Name Inputted");
        Log.v("EditorActivity", "Inputted mIntNameWeight : " + nIntNameWeight);


        //

        //Get the database in write mode
        //SQLiteDatabase dbUpdate = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        try {
            if(imageUri != null ) {
                mNamePicture = imageUri.toString();
                String petAddress= mNamePicture.substring(mNamePicture.lastIndexOf("/"+ 1));
                String[] petAdd= petAddress.split("/");
                mIntNamePicture = Integer.parseInt(petAdd[1]);
                Log.v("EditorActivity", "Inputted mNamePicture Address  : "
                        +petAdd[1]);
                values.put(PetEntry.COLUMN_PICTURE, mNamePicture);
            }

            else if(fileUri != null ) {
                String uriPhotoCheck =fileUri.getPath();
                Log.v(TAG, " onActivityResult():u " + uriPhotoCheck);
                Uri imageUroo =Uri.fromFile(new File(String.valueOf(uriPhotoCheck)));
                Log.v(TAG, " onActivityResult():u " + imageUroo);
                values.put(PetEntry.COLUMN_PICTURE, imageUroo.toString());
            }
            else {
                values.put(PetEntry.COLUMN_PICTURE, mIntNamePicture);
            }
        }catch (Exception err){
            Log.v("EditorActivity","Error encounter while updating data : ");
        }
        values.put(PetEntry.COLUMN_NAME, nNamePet);
        values.put(PetEntry.COLUMN_BREED, nNameBreed);

        values.put(PetEntry.COLUMN_WEIGHT, nIntNameWeight);

        if(mNameEditText.length()>3){

            String selection = PetEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(currentPetUri))};
            //dbUpdate.update(PetEntry.TABLE_NAME,values,selection,selectionArgs);
            getContentResolver().update(PetEntry.CONTENT_URI,values,selection,selectionArgs);
        }



    }

    private boolean validate() {
        boolean matchText = true;


        try {
            //Check that the Name of Pet is not null
            if (mIntNamePicture < 0) {

                methodInUse.toastChangeL(EditorActivity.this, "Invalid " + mPetT);
                matchText = false;
            }

            //Check that the Name of Pet is not null
            if (mNameEditText.length() < 3) {
                methodInUse.editLengthCheck(mNameEditText, mNameT, 3);
                methodInUse.toastChangeL(EditorActivity.this, "Invalid " + mNameT);
                matchText = false;
            }

            //Check that the Breed is not null
            if (mBreedEditText.length() < 3) {
                methodInUse.editLengthCheck(mBreedEditText, mBreedT, 3);
                methodInUse.toastChangeL(EditorActivity.this, "Invalid " + mBreedT);
                matchText = false;
            }

            //Check that the Weight is not null
            if (mWeightEditText.length() < 1) {
                methodInUse.editLengthCheck(mWeightEditText, mWeightT, 1);
                methodInUse.toastChangeL(EditorActivity.this, "Invalid " + mWeightT);
                matchText = false;
            }
        } catch (Exception err) {
            methodInUse.toastChangeL(EditorActivity.this, "Error encounter during data validation");

        }
        return matchText;
    }


    private boolean validateUpdateId() {
        boolean matchText = true;


        try {


            //Check that the Weight is not null
            if (petID == -1 || petID == 0) {

                methodInUse.toastChangeL(EditorActivity.this, "Pet detail can not be updated ");
                matchText = false;
            }
        } catch (Exception err) {
            methodInUse.toastChangeL(EditorActivity.this, "Error encounter during data validation");

        }
        return matchText;
    }


    /*This method returns a string if there is match with respect to the inputted
    text through oldNameEditText
    */
    private String checkFindName(){
        String namePetCheck = null;
        for(int i=0; i<dataQureyNameColumn().length;i++){

            Log.v("EditorActivity", "" + dataQureyNameColumn()[i]);
            if(dataQureyNameColumn()[i].equalsIgnoreCase(mNameEditText.getText().toString().trim())){
                namePetCheck=mNameEditText.getText().toString().trim();
                break;
            }
        }
        return namePetCheck;
    }

    public Intent takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);
                uriPhoto = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);

            } catch (IOException e) {
            }
        }
        return intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhoto());

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }


    private void checkExif(ExifInterface dd){
        int aa= 0;

        if(dd.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==3){


            bFromGallery.setRotation(180);


        }
        else if(dd.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==6){

            bFromGallery.setRotation(90);

        }

        else if(dd.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==8){
            bFromGallery.setRotation(270);

        }else{
            bFromGallery.setRotation(0);
        }
        imageUri =Uri.fromFile(new File(String.valueOf(mCurrentPhotoPath)));
        bSetImageCenter.setVisibility(View.GONE);
        bFromGallery.setImageURI(imageUri);

    }


    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}