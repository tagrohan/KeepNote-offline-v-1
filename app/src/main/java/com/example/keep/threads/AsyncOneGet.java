package com.example.keep.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.keep.views.CreateNote;
import com.example.keep.doa.NoteDatabase;
import com.example.keep.entities.Note;

@SuppressLint("StaticFieldLeak")
public class AsyncOneGet extends AsyncTask<Integer, Void, Note> {


    public interface OnOneDownload {
        void onOneDownload(Note note);
    }

    private final Context context;
    private NoteDatabase noteDatabase;
    private final OnOneDownload callback;
    private static final String TAG = "AsyncOneGet";

    public AsyncOneGet(Context context) {
        this.context = context;
        callback = (CreateNote) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        noteDatabase = NoteDatabase.getDatabase(context);
    }

    @Override
    protected void onPostExecute(Note note) {
        Log.d(TAG, "onPostExecute: " + note);
        callback.onOneDownload(note);
    }

    @Override
    protected Note doInBackground(Integer... integers) {

//        try {
//            Log.d(TAG, "doInBackground: called");
        Log.d(TAG, "doInBackground: "+integers[0]);
        return noteDatabase.keepDao().getNote(integers[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }
}
