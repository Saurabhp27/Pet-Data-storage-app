package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PetDBHelper  extends SQLiteOpenHelper {

    public static final int Version = 1;
    public static final String Database_Name = "petDB.db";

    public static final String Creae_SQL_Enteries = " CREATE TABLE "  + PetContract.PetEntry.Table_name + "(" +
            PetContract.PetEntry._ID + " INTEGER PRIMARY KEY," + PetContract.PetEntry.Column_name + " TEXT NOT NULL,"+
            PetContract.PetEntry.Column_breed + " TEXT," + PetContract.PetEntry.Column_gender + " INTEGER," + PetContract.PetEntry.Column_weight + " INTEGER );";

    public PetDBHelper(@Nullable Context context) {
        super(context, Database_Name , null , Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Creae_SQL_Enteries);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
