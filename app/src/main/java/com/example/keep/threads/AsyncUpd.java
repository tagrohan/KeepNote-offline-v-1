package com.example.keep.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.keep.views.CreateNote;
import com.example.keep.doa.NoteDatabase;
import com.example.keep.entities.Note;

public class AsyncUpd extends AsyncTask<Note, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final NoteDatabase noteDatabase;

    public interface OnOneUpdate {
        void onOneUpdate(boolean bool);
    }

    private OnOneUpdate onOneUpdate;


    public AsyncUpd(Context context) {
        this.context = context;
        noteDatabase = NoteDatabase.getDatabase(context);
        this.onOneUpdate = (CreateNote) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Note... notes) {
        try {
//            noteDatabase.keepDao().deleteNote(notes[0]);
            noteDatabase.keepDao().updateNote(notes[0]);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            Toast.makeText(context, "updated", Toast.LENGTH_SHORT).show();
            onOneUpdate.onOneUpdate(aBoolean);
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
