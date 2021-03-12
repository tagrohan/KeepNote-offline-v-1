package com.example.keep.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.keep.R;
import com.example.keep.controllers.DialogClass;
import com.example.keep.controllers.Recycler;
import com.example.keep.entities.Note;
import com.example.keep.threads.AsyncGet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncGet.OnDataAvailable {
    FloatingActionButton addNote;
    public static final int REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<Note> notes;
    Recycler recycler;
    SwipeRefreshLayout swipe;
    SearchView searchView;
    ImageView darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigationNew));
        }
        setContentView(R.layout.activity_main);
        //setting up navigation transparent
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //id's and listeners
        recyclerView = findViewById(R.id.recyclerView);
        addNote = findViewById(R.id.addNotes);

        swipe = findViewById(R.id.refresh);
        searchView = findViewById(R.id.searchBox);

        swipe.setRefreshing(true);
        //calling async
        new AsyncGet(MainActivity.this).execute();

        addNote.setOnClickListener(this);


        // dark mode implementation
        darkMode = findViewById(R.id.darkMode);

        darkMode.setOnLongClickListener(view -> {
            DialogClass dialog = new DialogClass(MainActivity.this, notes);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            return true;
        });


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //calling main asyncTask
                new AsyncGet(MainActivity.this).execute();
            }
        });
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recycler.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.addNotes) {
            startActivityForResult(new Intent(MainActivity.this, CreateNote.class), REQUEST_CODE);
        } else {
            Toast.makeText(MainActivity.this, "problem in switch case", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null && recycler != null) {
            Note note = (Note) data.getSerializableExtra("NOTE");
            Log.d(TAG, "onActivityResult: " + note);
            notes.add(0, note);
            recycler.notifyItemInserted(0);
        } else if (resultCode == RESULT_OK && requestCode == 200 && data != null && recycler != null) {
            //call to get updated data
            new AsyncGet(MainActivity.this).execute();
            Log.d(TAG, "onActivityResult: called update block");
        }
    }

    @Override
    public void notesAvailable(List<Note> notes) {
        Log.d(TAG, "notesAvailable: " + notes);
        this.notes = notes;
        invokeAdapter(notes);
        swipe.setRefreshing(false);
    }

    void invokeAdapter(List<Note> notes) {
        if (notes != null) {
            recycler = new Recycler(notes, MainActivity.this);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(recycler);
        } else {
            Log.d(TAG, "invokeAdapter: not called");
        }
    }
}