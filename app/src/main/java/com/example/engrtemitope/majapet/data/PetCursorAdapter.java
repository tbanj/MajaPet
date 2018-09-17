package com.example.engrtemitope.majapet.data;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.engrtemitope.majapet.R;

import java.io.File;

import static android.content.ContentValues.TAG;

public class PetCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: Fill out this method
        // Find fields to populate in inflated template
        TextView petBody = view.findViewById(R.id.name);
        TextView petPriority = view.findViewById(R.id.summary);
        ImageView petPicture = view.findViewById(R.id.pet_icon);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_BREED));
//        Uri pet_icon = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PICTURE)));
//
        Log.v("PetCursorAdapter", "Inputted mNamePet  : " +
                cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PICTURE)));
        String pet_icon = (cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PICTURE)));


        // Populate fields with extracted properties
        petBody.setText(name);
        petPriority.setText(String.valueOf(summary));
        // petPicture.setImageURI(pet_icon);


        try {

            if(pet_icon.startsWith("file") || pet_icon.startsWith("content") || pet_icon.startsWith("android")) {
                // int petDummyPic = Integer.parseInt(pet_icon);
                Uri fileUri= Uri.parse(pet_icon);
                if (pet_icon.startsWith("content")){
                    fileUri= Uri.parse(pet_icon);




                    petPicture.setImageURI(fileUri);
                    petPicture.setVisibility(View.VISIBLE);
                    Log.v("PetCursorAdapter","image disappear : "+ pet_icon);

                }

                else if (pet_icon.startsWith("android")){

                    try {
                        byte [] encodeByte=Base64.decode(pet_icon, Base64.DEFAULT);
                        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        petPicture.setImageBitmap(bitmap);
                        petPicture.setVisibility(View.VISIBLE);
                        Log.v("PetCursorAdapter","image disappear : "+ pet_icon);
                    } catch(Exception e) {
                        e.getMessage();

                    }
                }


                 else if (pet_icon.startsWith("file")){
                    String imageRotateCheck= fileUri.getPath();

                    // is use to check if the picture is distorted
                    ExifInterface exif = new ExifInterface(imageRotateCheck);
                    Log.v(TAG, " onActivityResult():u " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1));

                    if(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==3){

                        petPicture.setRotation(180);
                        petPicture.setImageURI(fileUri);
                        petPicture.setVisibility(View.VISIBLE);
                        Log.v("PetCursorAdapter","image disappear : "+ pet_icon);

                    }

                    else if(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==6){

                        petPicture.setRotation(90);
                        petPicture.setImageURI(fileUri);
                        petPicture.setVisibility(View.VISIBLE);
                        Log.v("PetCursorAdapter","image disappear : "+ pet_icon);

                    }

                    else if(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1)==8){

                        petPicture.setRotation(270);
                        petPicture.setImageURI(fileUri);
                        petPicture.setVisibility(View.VISIBLE);
                        Log.v("PetCursorAdapter","image disappear : "+ pet_icon);

                    }
                    petPicture.setImageURI(fileUri);
                    petPicture.setVisibility(View.VISIBLE);
                    Log.v("PetCursorAdapter","image disappear : "+ pet_icon);
                }



            }
            else if(Integer.parseInt(pet_icon) == 2131230836 || Integer.parseInt(pet_icon) == 2131230835) {
                petPicture.setImageResource(Integer.parseInt(pet_icon));
                petPicture.setVisibility(View.VISIBLE);
            }

            else if(pet_icon.startsWith("con")) {
                // int petDummyPic = Integer.parseInt(pet_icon);
                Uri fileUri= Uri.parse(pet_icon);
                // String imageRotateCheck= fileUri.getPath();




                petPicture.setImageURI(fileUri);
                petPicture.setVisibility(View.VISIBLE);
                Log.v("PetCursorAdapter","image disappear : "+ pet_icon);
            }

            else if(pet_icon.startsWith("/storage")) {
                // int petDummyPic = Integer.parseInt(pet_icon);
//                Uri fileUri= Uri.parse(pet_icon);
                Uri imageUroo =Uri.fromFile(new File(String.valueOf(pet_icon)));
                // String imageRotateCheck= fileUri.getPath();




                petPicture.setImageURI(imageUroo);
                petPicture.setVisibility(View.VISIBLE);
                Log.v("PetCursorAdapter","image disappear : "+ pet_icon);
            }


            else if(pet_icon.startsWith("/external")) {

                Uri imageUroo =Uri.fromFile(new File(String.valueOf(pet_icon)));
                               petPicture.setImageURI(imageUroo);
                petPicture.setVisibility(View.VISIBLE);
                Log.v("PetCursorAdapter","image disappear : "+ pet_icon);
            }

        }catch (Exception err){
            Log.v("PetCursorAdapter","Error encounter while populating data : ");
        }

    }


}