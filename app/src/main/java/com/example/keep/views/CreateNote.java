package com.example.keep.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.keep.R;
import com.example.keep.entities.Note;
import com.example.keep.threads.AsyncOneGet;
import com.example.keep.threads.AsyncSave;
import com.example.keep.threads.AsyncUpd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;


public class CreateNote extends AppCompatActivity implements View.OnClickListener, AsyncOneGet.OnOneDownload, AsyncSave.AfterSave
        , AsyncUpd.OnOneUpdate {
    private EditText title, subTitle, noteText;
    ShapeableImageView showingImage;
    private static final String TAG = "CreateNote";
    private View verticalLive;
    private String saveColor;
    private boolean isSave = true;
    private Note note;
    private final int PERMISSION_CODE = 1;
    public final int ACCESS_CODE = 2;
    private String image;
    private ImageView saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigationNew));
        }

        setContentView(R.layout.activity_create_note);
        //setting navigation bar color
//        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.quickActionBackground));
        //id's
        findViewById(R.id.backButton).setOnClickListener(this);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(this);


        findViewById(R.id.viewColor1).setOnClickListener(this);
        findViewById(R.id.viewColor2).setOnClickListener(this);
        findViewById(R.id.viewColor3).setOnClickListener(this);
        findViewById(R.id.viewColor4).setOnClickListener(this);
        findViewById(R.id.viewColor5).setOnClickListener(this);

        title = findViewById(R.id.inputNoteTitle);
        subTitle = findViewById(R.id.noteSubtitle);
        noteText = findViewById(R.id.inputNote);
        verticalLive = findViewById(R.id.verticalLine);
        showingImage = findViewById(R.id.showingImage);
        saveColor = "#1D8DEE";


        int id = getIntent().getIntExtra("INT_ID", -1);

        if (id != -1) {
            Log.d(TAG, "onCreate: " + id);
            new AsyncOneGet(CreateNote.this).execute(id);
        }
        intiMethod();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backButton) {
            onBackPressed();
        } else if (view.getId() == R.id.saveBtn) {
            validation();
            saveBtn.setEnabled(false);
        } else if (view.getId() == R.id.viewColor1) {
            saveColor = "#C626FC";
            setScreenColor(saveColor);
        } else if (view.getId() == R.id.viewColor2) {
            saveColor = "#0CEAED";
            setScreenColor(saveColor);
        } else if (view.getId() == R.id.viewColor3) {
            saveColor = "#9A3AFD";
            setScreenColor(saveColor);
        } else if (view.getId() == R.id.viewColor4) {
            saveColor = "#0FF8B3";
            setScreenColor(saveColor);
        } else if (view.getId() == R.id.viewColor5) {
            saveColor = "#ED4570";
            setScreenColor(saveColor);
        }
        // making it enable after 2 sec so that user don't hit save save two times
        new Handler().postDelayed(() -> saveBtn.setEnabled(true), 2000);
    }
    //start here

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateNote.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(CreateNote.this)
                    .setTitle("need permission")
                    .setMessage("dede bhai permission")
                    .setPositiveButton("Sure", (dialogInterface, i) -> ActivityCompat.requestPermissions(CreateNote.this, new String[]{Manifest.permission
                            .READ_EXTERNAL_STORAGE}, PERMISSION_CODE)).setNegativeButton("Nope", null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(CreateNote.this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            openGallery();
        } else {
            Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, ACCESS_CODE);
        } else {
            Toast.makeText(this, "else called", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Toast.makeText(this, "we are in boys", Toast.LENGTH_SHORT).show();
                Uri imageUri = data.getData();
                Log.e(TAG, "onActivityResult: --------------->" + imageUri);
                if (imageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        showingImage.setImageBitmap(bitmap);
                        showingImage.setVisibility(View.VISIBLE);

                        //storing into byte format

//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
//                        image = stream.toByteArray();
                        this.image = getPathFromUri(imageUri);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(this, "omg", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getPathFromUri(Uri uri) {
        String filePath;

        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor == null) {
            filePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
        }
        Log.d(TAG, "getPathFromUri: ------------------> filepath" + filePath);
        return filePath;
    }


    //end here
    public void validation() {
        if (isEmpty(title)) {
            title.setError("enter title");
        }
//        else if (isEmpty(subTitle)) {
//            subTitle.setError("enter subtitle");
//        }
        else {
            Note note = new Note(title.getText().toString().trim(), subTitle.getText().toString().trim()
                    , noteText.getText().toString().trim(), saveColor);
            //save image as byte here
            note.setImage(image);

            Log.d(TAG, "validate: color code--------->" + note.getImage());
            if (!isSave) {
                note.setId(this.note.getId());
                Log.d(TAG, "validation: setId()called");
            }

            saveOrUpdate(note, isSave);

            Log.d(TAG, "validation: " + note);
        }
    }

    public void saveOrUpdate(Note note, boolean isSave) {
        if (isSave) {
            new AsyncSave(CreateNote.this).execute(note);
            this.note = note;
        } else {
            Log.d(TAG, "saveOrUpdate: " + note.getImage());
            new AsyncUpd(CreateNote.this).execute(note);
        }
    }

    private void intiMethod() {
        ConstraintLayout layout = findViewById(R.id.option_main);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout);

        layout.findViewById(R.id.bottom_sheet_view).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        layout.findViewById(R.id.addImage).setOnClickListener(view -> {
            Toast.makeText(CreateNote.this, "addImage called", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(CreateNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CreateNote.this, "already granted", Toast.LENGTH_SHORT).show();
                openGallery();
            } else {
                requestPermission();
            }
        });


    }


    public boolean isEmpty(EditText text) {
        return text.getText().toString().trim().isEmpty();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //setting up colors
    private void setScreenColor(String color) {
        int mainColor = Color.parseColor(color);
        GradientDrawable gradientDrawable = (GradientDrawable) verticalLive.getBackground();
        gradientDrawable.setColor(mainColor);
        //lets see its working or not
        findViewById(R.id.create_main).setBackgroundColor(mainColor);
    }

    @Override
    public void onOneDownload(Note note) {
        if (note != null) {
            title.setText(note.getTitle());
            subTitle.setText(note.getSubTitle());
            noteText.setText(note.getNoteText());

            setScreenColor(note.getColor());

            isSave = false;
            this.note = note;
            this.image = note.getImage();
            Log.d(TAG, "onOneDownload: color code--------->" + this.note.getImage());
            if (note.getImage() != null) {
                Glide.with(this)
                        .load(note.getImage())
                        .into(showingImage);
            }
        }
    }

    @Override
    public void afterSave(boolean isSave) {
        //going back to main
        if (isSave) {
            Intent intent = new Intent();
            intent.putExtra("NOTE", this.note);
            setResult(RESULT_OK, intent);
            Log.d(TAG, "afterSave: called");
        }
        finish();
    }

    @Override
    public void onOneUpdate(boolean bool) {
        if (bool) {
            Log.d(TAG, "onOneUpdate: called");
            Intent intent = new Intent();
            intent.putExtra("NOTE", this.note);
            Log.d(TAG, "saveOrUpdate: called");
            setResult(RESULT_OK, intent);

        } else {
            Log.d(TAG, "onOneUpdate: problem hai update me");
        }
        finish();
    }
}