package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.util.Log;
import android.widget.Toast;

public class petprovider extends ContentProvider {

    public static final String LOG_TAG = petprovider.class.getSimpleName();

    private PetDBHelper mdbhelper;

    private static final int PET = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.Path_pets,PET);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.Path_pets + "/#",PET_ID);

        // TODO: Add 2 content URIs to URI matcher
    }


    public boolean onCreate() {
mdbhelper = new PetDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mdbhelper.getReadableDatabase();

        Cursor c;

        int match = sUriMatcher.match(uri);

        switch (match){
            case PET:
                c = db.query(PetContract.PetEntry.Table_name,projection,selection,selectionArgs,null,null, null);
                break;

            case PET_ID:
                selection = PetContract.PetEntry.Column_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
              c =    db.query(PetContract.PetEntry.Table_name,projection,selection, selectionArgs, null,null,null);
        break;
            default:throw new IllegalArgumentException("unknown query" + uri);
        }

        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c ;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PET:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);

        switch (match){
            case PET:
                return insertpet(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported" + uri);
        }
    }

    private Uri insertpet (Uri uri, ContentValues values){

        String name = values.getAsString(PetContract.PetEntry.Column_name);

        if (name== null || name.isEmpty()){
//            Toast.makeText(getContext(), "Please Enter the name", Toast.LENGTH_SHORT).show();
          throw new IllegalArgumentException("null values not allowed");
        }

        Integer weight = values.getAsInteger(PetContract.PetEntry.Column_weight);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase db = mdbhelper.getWritableDatabase();
        long id = db.insert(PetContract.PetEntry.Table_name,null , values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mdbhelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowdeleted;
        switch (match) {


            case PET:
                rowdeleted = db.delete(PetContract.PetEntry.Table_name, selection, selectionArgs);
                break;
            case PET_ID:

                selection = PetContract.PetEntry.Column_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                 rowdeleted =  db.delete(PetContract.PetEntry.Table_name, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("invalid Uri to delete");
        }

        if(rowdeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case PET:
                return updatepet(uri,values,selection,selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry.Column_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatepet(uri,values,selection,selectionArgs);

        }

        return 0;
    }

    public int updatepet(Uri uri, ContentValues values, String selection, String [] selectionargs) {

        if (values.containsKey(PetContract.PetEntry.Column_name)) {
            String name = values.getAsString(PetContract.PetEntry.Column_name);
            if (name == null) {
                throw new IllegalArgumentException("Pet require a name");
            }
        }

        if (values.containsKey(PetContract.PetEntry.Column_weight)){
            Integer weight = values.getAsInteger(PetContract.PetEntry.Column_weight);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
    }



        if (values.size()==0){
            return 0;
        }

        SQLiteDatabase db = mdbhelper.getWritableDatabase();

        int rowupdated =  db.update(PetContract.PetEntry.Table_name,values,selection,selectionargs);

        if(rowupdated!= 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowupdated;
    }
}
