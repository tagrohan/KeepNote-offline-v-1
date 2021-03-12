package com.example.keep.doa;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.keep.entities.Note;

import java.util.List;

@Dao
public interface KeepDao {

    @Query("SELECT * FROM note_table ORDER BY id Desc")
    List<Note> getNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Update
    void updateNote(Note note);


    @Query("SELECT * FROM note_table WHERE id = :pos")
    Note getNote(Integer pos);

    @Query("UPDATE note_table SET title =:title , subTitle=:subTitle , dateTime=:dateTime, noteText=:noteText, color=:color WHERE id =:id ")
    void updateViaQuery(String title, String subTitle, String dateTime, String noteText, String color, Integer id);
}
