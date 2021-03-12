package com.example.keep.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.keep.controllers.Recycler;
import com.example.keep.doa.NoteDatabase;

public class AsyncDel extends AsyncTask<Integer, Void, Boolean> {

    public interface IsDeleted {
         void isDeleted(Boolean isDeleted);
    }

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private NoteDatabase database;
    private static final String TAG = "AsyncDel";
    private final IsDeleted isDeletedCallBack;

    public AsyncDel(Context context, Recycler.ViewHolder viewHolder) {
        this.context = context;
        this.isDeletedCallBack = (IsDeleted) viewHolder;
    }

    @Override
    protected void onPreExecute() {
        database = NoteDatabase.getDatabase(context);
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {

        try {
            database.keepDao().deleteNote(database.keepDao().getNote(integers[0]));
            Log.d(TAG, "doInBackground: called");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        isDeletedCallBack.isDeleted(aBoolean);
        Log.d(TAG, "onPostExecute: called"+aBoolean );
    }
}
