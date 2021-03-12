package com.example.keep.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.keep.views.CreateNote;
import com.example.keep.doa.NoteDatabase;
import com.example.keep.entities.Note;

public class AsyncSave extends AsyncTask<Note, Void, Boolean> {
    private NoteDatabase database;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private static final String TAG = "Async";
    private AfterSave callaback;

    public interface AfterSave {
        void afterSave(boolean bool);
    }

    public AsyncSave(Context context) {
        this.context = context;
        this.callaback = (CreateNote) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        database = NoteDatabase.getDatabase(context);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();
            callaback.afterSave(aBoolean);
        } else {
            Toast.makeText(context, "something  wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Boolean doInBackground(Note... notes) {
        if (database != null) {
            database.keepDao().insertNote(notes[0]);
            Log.d(TAG, "doInBackground: " + notes[0]);
            return true;
        } else {
            return false;
        }
    }
}
