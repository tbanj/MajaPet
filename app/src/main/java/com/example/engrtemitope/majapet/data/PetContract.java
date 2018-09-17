package com.example.engrtemitope.majapet.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Engr. Temitope on 3/15/2018.
 */

public final class PetContract {
    private PetContract() {
    }

    public static abstract class PetEntry implements BaseColumns{
        public static final String TABLE_NAME = "Pets";

        //._ID it means is from BaseColumns class
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PICTURE= "picture";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED= "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT= "weight";


        public static final String CONTENT_SCHEMA= "content://";
        public static final String PATH_PETS= "Pets";

        public static final String CONTENT_AUTHORITY= "com.example.engrtemitope.majapet";


        public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEMA + CONTENT_AUTHORITY);
        //to ascertain Uri from you need to append BASE_CONTENT_URI with PATH_PETS
        public static final Uri CONTENT_URI =Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;
        /*
        * Possible values for Gender
        * */
        public static final int GENDER_MALE= 1;
        public static final int GENDER_FEMALE= 2;
        public static final int GENDER_UNKNOWN= 0;

        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }
}
