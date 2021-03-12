package com.example.keep.controllers;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.keep.R;
import com.example.keep.views.CreateNote;
import com.example.keep.views.MainActivity;
import com.example.keep.entities.Note;
import com.example.keep.threads.AsyncDel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Recycler extends RecyclerView.Adapter<Recycler.ViewHolder> implements Filterable {

    private List<Note> notes;
    private List<Note> notesAll;
    private final MainActivity context;
    private static final String TAG = "Recycler";

    public Recycler(List<Note> notes, MainActivity context) {
        this.notes = notes;
        notesAll = new ArrayList<>(notes);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.noteview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.setNote(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Note> filteredNotes = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                filteredNotes.addAll(notesAll);
            }else {
                for (Note note:notesAll) {
                    if(note.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredNotes.add(note);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredNotes;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notes.clear();
            notes.addAll((Collection<? extends Note>) filterResults.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
            , AsyncDel.IsDeleted {
        TextView title, subtitle, description;
        CardView layout;
        ImageView mainActivityImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            subtitle = itemView.findViewById(R.id.text_subtitle);
            description = itemView.findViewById(R.id.text_description);
            layout = itemView.findViewById(R.id.linear_layout);
            mainActivityImage = itemView.findViewById(R.id.mainActivityImage);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void setNote(Note note) {

            title.setText(note.getTitle());
            subtitle.setText(note.getSubTitle());
            description.setText(note.getNoteText());

            if (note.getColor() != null) {
//                layout.setBackgroundColor(Color.parseColor(note.getColor()));
                try {
                    layout.setCardBackgroundColor(Color.parseColor(note.getColor()));
                } catch (Exception e) {
                    Log.d(TAG, "setNote: image have some problem" + e);
                }
            }
            if (note.getImage() != null) {
                try {
                    Glide.with(context)
                            .load(note.getImage())
                            .into(mainActivityImage);
                    mainActivityImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mainActivityImage.setVisibility(View.INVISIBLE);
                Log.d(TAG, "setNote: koi image path nhi save hua h");
            }
        }

        @Override
        public void onClick(View view) {
            Pair<View, String> pair = new Pair<>(view, "noteview_to_create_note");
            Intent intent = new Intent(context, CreateNote.class);
            Note note = notes.get(getAdapterPosition());
            intent.putExtra("INT_ID", note.getId());
            // adding shared animation to the create view
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, pair);
            context.startActivityForResult(intent, 200, options.toBundle());
        }

        @Override
        public boolean onLongClick(View view) {
            android.app.AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete!")
                    .setMessage("Are you sure")
                    .setNegativeButton("cancel", null)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // delete call back from here
                            new AsyncDel(context, ViewHolder.this).execute(notes.get(getAdapterPosition()).getId());
                        }
                    }).show();

            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogButton));
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogButton));
            return true;
        }

        @Override
        public void isDeleted(Boolean isDeleted) {
            if (isDeleted) {
                notes.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            }

//            Log.d(TAG, "isDeleted: called");
//            for (int i = 0; i <= 4; i+=2) {
//                notes.remove(i);
//                notifyItemRemoved(i);
//            }
        }
    }

}
