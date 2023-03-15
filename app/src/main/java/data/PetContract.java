package data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class PetContract {

    private PetContract () {}
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri Base_content_Uri = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String Path_pets = "Pet";

    public static final class PetEntry implements BaseColumns {

        public static final String Table_name = "Pet";
        public static final String Column_ID = BaseColumns._ID;
        public static final String Column_name = "name";
        public static final String Column_breed = "breed";
        public static final String Column_gender = "gender";
        public static final String Column_weight = "weight";

        public static final int gender_male = 1;
        public static final int gender_female = 2;
        public static final int gender_unknown = 0;

        public static boolean isValidGender(int gender) {
            if (gender == gender_unknown || gender == gender_male || gender == gender_female) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets and single item
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + Path_pets;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + Path_pets;




        public static final Uri Content_Uri = Uri.withAppendedPath(Base_content_Uri,Path_pets);
    }
}