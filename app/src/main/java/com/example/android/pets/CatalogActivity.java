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
package com.example.android.pets;

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
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.channels.AsynchronousCloseException;

import data.PetContract;
import data.PetDBHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int Loader_id = 0;
    Petcursoradapter mcursoradapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

          //creating connection///
//        PetDBHelper mdbHelper = new PetDBHelper(this);
//        SQLiteDatabase db= mdbHelper.getReadableDatabase();



        ListView petlistview = (ListView) findViewById(R.id.list);

        petlistview.setEmptyView(findViewById(R.id.empty_view));  // when list is empty
//
     mcursoradapter = new Petcursoradapter(this,null);
    petlistview.setAdapter(mcursoradapter);

        getLoaderManager().initLoader(Loader_id,null, this);

        petlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i =new Intent (CatalogActivity.this,EditorActivity.class);
                Uri currentpeturi = ContentUris.withAppendedId(PetContract.PetEntry.Content_Uri,id);
                i.setData(currentpeturi);
                startActivity(i);
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.Column_name,"Totto");
        values.put(PetContract.PetEntry.Column_breed,"Terrier");
        values.put(PetContract.PetEntry.Column_gender,"male");
        values.put(PetContract.PetEntry.Column_weight,"7KG");
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
//                insertpet();
                getContentResolver().insert(PetContract.PetEntry.Content_Uri,values);
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete all entries" menu option

            case R.id.action_delete_all_entries:
                // Do nothing for now
                getContentResolver().delete(PetContract.PetEntry.Content_Uri,null,null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public int updatepettemporary(){
//
//        Uri uri = Uri.parse(PetContract.PetEntry.Content_Uri + "/3");
//        ContentValues values = new ContentValues();
//        values.put(PetContract.PetEntry.Column_name,"Tate");
//        values.put(PetContract.PetEntry.Column_breed,"T");
//        values.put(PetContract.PetEntry.Column_gender,"female");
//        values.put(PetContract.PetEntry.Column_weight,"15KG");
//
//       return getContentResolver().update(uri,values,null,null);
//    }

//    public void insertpet(){
//        ContentValues values = new ContentValues();
//        values.put(PetContract.PetEntry.Column_name,"Totto");
//        values.put(PetContract.PetEntry.Column_breed,"Terrier");
//        values.put(PetContract.PetEntry.Column_gender,"male");
//        values.put(PetContract.PetEntry.Column_weight,"7KG");
//
//        PetDBHelper mdbHelper = new PetDBHelper(this);
//        SQLiteDatabase db = mdbHelper.getWritableDatabase();
//
//        long newRowId = db.insert(PetContract.PetEntry.Table_name,null, values);
//
//        if (newRowId == -1) {
//            // If the row ID is -1, then there was an error with insertion.
//            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
//        } else {
//            // Otherwise, the insertion was successful and we can display a toast with the row ID.
//            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
//        }







//private void displayDatabaseInfo(){
//
////    PetDBHelper mdbHelper = new PetDBHelper(this);
////    SQLiteDatabase db= mdbHelper.getReadableDatabase();
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//
//
//        String [] projection = {PetContract.PetEntry.Column_ID, PetContract.PetEntry.Column_name, PetContract.PetEntry.Column_breed, PetContract.PetEntry.Column_weight, PetContract.PetEntry.Column_gender};
//
////        Cursor c = db.query(PetContract.PetEntry.Table_name,projection,null,null,null,null,null);    //  .... dirrect contact with database , now using contentprovider below
//
////Uri uri = Uri.parse("content://com.example.android.pets/Pet/");
//        Cursor c = getContentResolver().query(PetContract.PetEntry.Content_Uri, projection, null, null, null);
//
//    ListView petlistview = (ListView) findViewById(R.id.list);
//
//    Petcursoradapter adapter = new Petcursoradapter(this,c);
//
//    petlistview.setAdapter(adapter);
//
//    petlistview.setEmptyView(findViewById(R.id.empty_view));  // when list is empty
//
//
//            }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {PetContract.PetEntry.Column_ID, PetContract.PetEntry.Column_name, PetContract.PetEntry.Column_breed};

        return new CursorLoader(this, PetContract.PetEntry.Content_Uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mcursoradapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mcursoradapter.swapCursor(null);
    }
}

