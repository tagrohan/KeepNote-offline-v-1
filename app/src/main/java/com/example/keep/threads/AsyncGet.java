package com.example.keep.threads;

import android.content.Context;
import android.os.AsyncTask;

import com.example.keep.views.MainActivity;
import com.example.keep.doa.NoteDatabase;
import com.example.keep.entities.Note;

import java.util.List;

public class AsyncGet extends AsyncTask<Void, Void, List<Note>> {
    private final Context context;
    private NoteDatabase database;
    OnDataAvailable onDataAvailable;

    public interface OnDataAvailable {
        void notesAvailable(List<Note> notes);
    }

    public AsyncGet(Context context) {
        this.context = context;
        onDataAvailable = (MainActivity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        database = NoteDatabase.getDatabase(context);
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        onDataAvailable.notesAvailable(notes);
    }

    @Override
    protected List<Note> doInBackground(Void... voids) {
        if (database != null) {
            List<Note> lists = database.keepDao().getNotes();
            return lists;
        } else {
            return null;
        }

    }
}
