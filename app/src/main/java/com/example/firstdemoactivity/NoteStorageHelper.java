package com.example.firstdemoactivity;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NoteStorageHelper {
    private static final String PREF_NAME = "note_pref";
    private static final String KEY_NOTES = "note_list";

    public static void saveNotes(Context context, ArrayList<Note> noteList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteList);
        editor.putString(KEY_NOTES, json);
        editor.apply();
    }

    public static ArrayList<Note> loadNotes(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_NOTES, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }
}
