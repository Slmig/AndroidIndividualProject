package com.example.androidindividualproject;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class DataManager {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String DATA_KEY = "data";
    private static final String TIME_KEY = "last_update_time";
    private static final String GOLD_KEY = "gold";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public DataManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public void SaveData(PetData data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTimeMillis = System.currentTimeMillis();
        editor.putLong(TIME_KEY, currentTimeMillis);
        String petJson = gson.toJson(data);
        editor.putString(DATA_KEY, petJson);
        editor.apply();
    }
    public long GetLastUpdateTime(){
        return sharedPreferences.getLong(TIME_KEY, System.currentTimeMillis());
    }
    public int GetGold(){
        return sharedPreferences.getInt(GOLD_KEY, 0);
    }
    public void SaveGold(int goldValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(GOLD_KEY, goldValue);
        editor.apply();
    }
    public PetData GetData(){
        String petJson = sharedPreferences.getString(DATA_KEY, null);
        if (petJson != null) {
            return gson.fromJson(petJson, PetData.class);
        }
        return new PetData();
    }
}

