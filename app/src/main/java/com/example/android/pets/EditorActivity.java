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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import data.PetContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */

    private boolean pethaschanged = false;

    private int Loader_id = 0;
    private int mGender = 0;
    private Uri currentpeturi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent i = getIntent();
        currentpeturi = i.getData();

        if(currentpeturi== null){
            setTitle("Add a Pet");
            invalidateOptionsMenu();
        }else{
            setTitle("Edit Pet");
            getLoaderManager().initLoader(Loader_id,null,this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        mNameEditText.setOnTouchListener(montouchlistener);
        mBreedEditText.setOnTouchListener(montouchlistener);
        mWeightEditText.setOnTouchListener(montouchlistener);
        mGenderSpinner.setOnTouchListener(montouchlistener);


    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
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
                        mGender = 1; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = 2; // Female
                    } else {
                        mGender = 0; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentpeturi == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if(currentpeturi==null){
                insertpet();
                } else
                {
                    savepet();
                }

                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!pethaschanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showunsavedchanges(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void savepet(){
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String sWeight = mWeightEditText.getText().toString().trim();
        int weight = Integer.parseInt(sWeight);

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.Column_name,name);
        values.put(PetContract.PetEntry.Column_breed,breed);
        values.put(PetContract.PetEntry.Column_gender,mGender);
        values.put(PetContract.PetEntry.Column_weight,weight);

        int updatedpet = getContentResolver().update(currentpeturi,values,null,null);

        if (updatedpet == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "update failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this,"Pet Updated",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void insertpet(){
       String name = mNameEditText.getText().toString().trim();
       String breed = mBreedEditText.getText().toString().trim();
       String sWeight = mWeightEditText.getText().toString().trim();
//       int weight = Integer.parseInt(sWeight);

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) &&
                TextUtils.isEmpty(sWeight) && mGender == PetContract.PetEntry.gender_unknown) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.Column_name,name);
        values.put(PetContract.PetEntry.Column_breed,breed);
        values.put(PetContract.PetEntry.Column_gender,mGender);
//        values.put(PetContract.PetEntry.Column_weight,weight);

        int weight = 0;
        if (!TextUtils.isEmpty(sWeight)) {
            weight = Integer.parseInt(sWeight);
        }
        values.put(PetContract.PetEntry.Column_weight, weight);


        Uri newuri = getContentResolver().insert(PetContract.PetEntry.Content_Uri,values);


        if (newuri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.insert_Pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
//        if (newRowId == -1) {
//            // If the row ID is -1, then there was an error with insertion.
//            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
//        } else {
//            // Otherwise, the insertion was successful and we can display a toast with the row ID.
//            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
//        }



    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection ={PetContract.PetEntry.Column_ID, PetContract.PetEntry.Column_name, PetContract.PetEntry.Column_breed, PetContract.PetEntry.Column_weight, PetContract.PetEntry.Column_gender};

        return new CursorLoader(this,currentpeturi,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (cursor.moveToFirst()){
        int namecolumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_name);
        int breedcolumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_breed);
        int gendercolumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_gender);
        int weightcolumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_weight);

        String name = cursor.getString(namecolumnindex);
        String breed = cursor.getString(breedcolumnindex);
        int gender = cursor.getInt(gendercolumnindex);
        int weight = cursor.getInt(weightcolumnindex);

        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mWeightEditText.setText(Integer.toString(weight));

        switch (gender){
            case PetContract.PetEntry.gender_male:
                mGenderSpinner.setSelection(1);
                break;
            case PetContract.PetEntry.gender_female:
                mGenderSpinner.setSelection(2);
                break;
            default:
                mGenderSpinner.setSelection(0);
                break;
        }

    }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }

private View.OnTouchListener montouchlistener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        pethaschanged = true;
        return false;
    }
};


    private void showunsavedchanges(
        DialogInterface.OnClickListener discardButtonClickListener) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!pethaschanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showunsavedchanges(discardButtonClickListener);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        getContentResolver().delete(currentpeturi,null,null);
        Toast.makeText(this,"Pet deleted",Toast.LENGTH_SHORT).show();

        finish();

    }

}

