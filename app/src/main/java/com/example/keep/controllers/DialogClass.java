package com.example.keep.controllers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.keep.R;
import com.example.keep.entities.Note;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DialogClass extends Dialog {
    MaterialButton button;
    TextView noOfNotes;
    String number;

    public DialogClass(@NonNull Context context, List<Note> notes) {
        super(context);
        if(notes != null){
            this.number = notes.size() + " notes";
        }else {
            this.number = "such empty";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view);
        button = findViewById(R.id.toggle);
        noOfNotes = findViewById(R.id.userNumberOfNotes);

        init();

    }

    private void init() {

        noOfNotes.setText(number);
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            button.setText("Light");
        }

        button.setOnClickListener(view -> {

            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

        });
    }
}
