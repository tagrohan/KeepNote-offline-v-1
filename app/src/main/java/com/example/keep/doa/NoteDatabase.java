package com.example.keep.doa;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.keep.entities.Note;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static volatile NoteDatabase database;

    public abstract KeepDao keepDao();

    public static NoteDatabase getDatabase(Context context){
        if(database == null){
            synchronized (NoteDatabase.class){
                if(database == null){
                    database = Room.databaseBuilder(context
                    ,NoteDatabase.class,"note_db")
                            .build();
                }
            }
        }
        return database;
    }

}
