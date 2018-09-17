package com.example.engrtemitope.majapet;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.engrtemitope.majapet.data.PetContract;

public class MethodInUse   {

    public MethodInUse(){}

    public void editLengthCheckF(EditText idEditTextt, String nameEx, int editCount){
        if(idEditTextt.getText().toString().trim()
                .length() <editCount || idEditTextt.getText().toString().trim()
                .length() >=1){
            idEditTextt.setError(nameEx+ " is required");
        }else if(idEditTextt.getText().toString().trim()
                .length() >=editCount ){
            idEditTextt.setError(null);
        }else{
            idEditTextt.setError(null);
        }

    }

    //This Method is use to check if EditText when is in Out of Focus Mode
    // which is when user is in another View .It check whether total input is below a particular string length
    // e.g  EditText<2
    public void editLengthCheck(EditText idEditText, String nameEx,int editCount){
        if(idEditText.getText().toString().trim()
                .length() < editCount){
            idEditText.setError(nameEx+ " is required");


        }
        else{
            idEditText.setError(null);
        }
    }

    //this is use to test & determine if there is no string inputted to EditText
    public boolean emptyTextEdit(EditText emptyEdit, String emptyTex){
        boolean matchText=true;
        if(TextUtils.isEmpty(emptyEdit.getText().toString().trim())){
            matchText=false;
        }
        return  matchText;
    }

    //Toast Message Method
    public void toastChangeL(Context context, String changeToastMessage){
        Toast toastPrivate= null;
        //for context variable input the java file name you are currently working on
        //changeToastMessage you will input text you want it to display
        toastPrivate.makeText(context,changeToastMessage,Toast.LENGTH_LONG).show();

    }

    //Is use to if EditText is not empty and details inputted is acceptable base on stipulated rule
    public void CheckingEditText(EditText edittest, String mNameError, int lowestCount, boolean hasFocusCheck) {
        if(hasFocusCheck){
            editLengthCheckF(edittest,mNameError,lowestCount);

        }
        else{
            editLengthCheck(edittest,mNameError,lowestCount);
        }
    }

    //this makes it possible to loop through all the data in each content of the columns in Pet table
    //
    public static String[] CursorPojection() {
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PICTURE,
                PetContract.PetEntry.COLUMN_NAME,
                PetContract.PetEntry.COLUMN_BREED,
                PetContract.PetEntry.COLUMN_GENDER,
                PetContract.PetEntry.COLUMN_WEIGHT};
        return projection;
    }


}
