package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import data.PetContract;

public class Petcursoradapter  extends CursorAdapter {
    public Petcursoradapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listi_tem,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvname = (TextView) view.findViewById(R.id.name);
        TextView tvsummary = (TextView) view.findViewById(R.id.summary);

        int namecoumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_name);
        int breedcolumnindex = cursor.getColumnIndex(PetContract.PetEntry.Column_breed);

        String name = cursor.getString(namecoumnindex);
        String breed = cursor.getString(breedcolumnindex);

        if(TextUtils.isEmpty(breed)){
            breed = "Unkown Breed";
        }

        tvname.setText(name);
        tvsummary.setText(breed);

    }
}
