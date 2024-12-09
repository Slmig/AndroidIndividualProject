package com.example.androidindividualproject;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class StatsCalculator {
    private final DataManager dataManager;
    private final long SECONDS_IN_A_DAY = 86400;
    private final double DAYS_TO_DIE_FROM_HUNGER = 1;
    private final double DAYS_TO_DIE_FROM_FATIGUE = 2;
    private final double DAYS_TO_DIE_FROM_SADNESS = 4;
    private Map<Long, Integer> coefficients = new HashMap<Long, Integer>() {{
        put(SECONDS_IN_A_DAY*7, 1);
        put(SECONDS_IN_A_DAY*14, 2);
        put(SECONDS_IN_A_DAY*30, 5);
    }};
    public StatsCalculator(Context context) {
        dataManager = new DataManager(context);
    }
    public PetData Recalculate(){
        double hungerBaseDelta = (double)100/(SECONDS_IN_A_DAY*DAYS_TO_DIE_FROM_HUNGER);
        double fatigueBaseDelta = (double)100/(SECONDS_IN_A_DAY*DAYS_TO_DIE_FROM_FATIGUE);
        double happinessBaseDelta = (double)100/(SECONDS_IN_A_DAY*DAYS_TO_DIE_FROM_SADNESS);
        PetData petData = dataManager.GetData();
        long lastUpdateTime = dataManager.GetLastUpdateTime();
        long timeDelta = (System.currentTimeMillis()-lastUpdateTime)/1000;
        petData.Hunger = Math.max(petData.Hunger-CalculateFullDelta(hungerBaseDelta, petData.LifeTime, timeDelta),0);
        petData.Fatigue = Math.max(petData.Fatigue-CalculateFullDelta(fatigueBaseDelta, petData.LifeTime, timeDelta),0);
        petData.Happiness = Math.max(petData.Happiness - CalculateFullDelta(happinessBaseDelta, petData.LifeTime, timeDelta),0);
        petData.LifeTime += timeDelta;
        dataManager.SaveData(petData);
        return petData;
    }
    private double CalculateFullDelta(double baseDelta, long lifetime, long timeDelta){
        double fullDelta = 0;
        for (Map.Entry<Long, Integer> entry : coefficients.entrySet()) {
            if (timeDelta <= 0) break;
            if (lifetime < entry.getKey()){
                long deltaTime = Math.min(timeDelta,entry.getKey()-lifetime);
                fullDelta += deltaTime * baseDelta * entry.getValue();
                lifetime += deltaTime;
                timeDelta -= deltaTime;
            }
        }
        return fullDelta;
    }
}
