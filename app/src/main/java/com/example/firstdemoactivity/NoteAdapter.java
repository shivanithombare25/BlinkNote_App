package com.example.firstdemoactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ArrayList<Note> noteList;
    private ArrayList<Note> fullNoteList;

    public NoteAdapter(ArrayList<Note> noteList) {
        this.noteList = new ArrayList<>(noteList);
        this.fullNoteList = new ArrayList<>(noteList);
    }
    private boolean showTrashedOnly = false;

    public NoteAdapter(ArrayList<Note> noteList, boolean showTrashedOnly) {
        this.noteList = new ArrayList<>(noteList);
        this.fullNoteList = new ArrayList<>(noteList);
        this.showTrashedOnly = showTrashedOnly;
    }

    public void filter(String text) {
        noteList.clear();
        for (Note note : fullNoteList) {
            if (!showTrashedOnly && note.isTrashed()) continue; // 👈 Skip trashed notes only in normal mode
            if (showTrashedOnly && !note.isTrashed()) continue; // 👈 Skip normal notes in trash mode

            if (text.isEmpty() ||
                    note.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    note.getDescription().toLowerCase().contains(text.toLowerCase())) {
                noteList.add(note);
            }
        }
        notifyDataSetChanged();
    }



    public interface OnItemClickListener {
        void onItemClick(Note note, int position);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public ArrayList<Note> getFullList() {
        return fullNoteList;
    }

    public ArrayList<Note> getCurrentList() {
        return noteList;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, descView, dateView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.note_title);
            descView = itemView.findViewById(R.id.note_description);
            dateView = itemView.findViewById(R.id.note_time); // ✅ Must match your layout
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleView.setText(note.getTitle());
        holder.descView.setText(note.getDescription());

        // ✅ Set formatted date/time
        holder.dateView.setText(formatDate(note.getUpdatedTime()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(note, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ✅ Format timestamp to readable string
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
