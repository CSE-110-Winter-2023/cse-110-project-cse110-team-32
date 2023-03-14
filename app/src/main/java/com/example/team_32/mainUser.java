package com.example.team_32;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class mainUser extends User{
    private static mainUser instance;
    public String private_code;
    private Gson gson;

    @Ignore
    public static mainUser singleton(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt){
        Log.d("nameActivity", "singleton: out");
        if(instance == null){
            Log.d("nameActivity", "singleton: in");
            instance = new mainUser(label, lat, lon, updatedAt);
//            Log.d("nameActivity", this.toJSON());
        }
        Log.d("nameActivity", "singleton: returned");
        return instance;
    }
    @Ignore
    public static  mainUser singleton(){
//        Log.i(TAG, "singleton: ");
        return instance;
    }

    @Ignore
    public static boolean exists(){
        Log.i("nameActivity", "exists: " + (instance != null));
        return (instance != null);
    }
    @Ignore
    public mainUser(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt) {
        super(label, lat, lon, updatedAt);
        this.private_code = String.valueOf(this.public_code + "_private2");
        gson = new GsonBuilder().setExclusionStrategies(new JsonExcPut()).setPrettyPrinting().create();
        Log.d("nameActivity", "worked out ?");
    }

    public static mainUser fromUser(User localMain) {
        return singleton(localMain.label, localMain.latitude, localMain.longitude, localMain.updatedAt);
    }

    @Ignore
    @Override
    public String toJSON() {
        return gson.toJson(this);
    }

    @Ignore
    public void updateLoc(@NonNull Double first, @NonNull Double second) {
        latitude =  first.floatValue();
        longitude =  second.floatValue();
    }
}

// Excluding: public_code, updatedAt
class JsonExcPut implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        var ret = f.getName().equals("private_code") ||f.getName().equals("label") || f.getName().equals("latitude")|| f.getName().equals("longitude");
        Log.i("Tested this", f.getName() +  " " + ret);

        return !ret;
    }

}
