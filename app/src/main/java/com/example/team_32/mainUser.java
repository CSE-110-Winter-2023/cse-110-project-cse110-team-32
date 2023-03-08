package com.example.team_32;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class mainUser extends User{
    private static mainUser instance;
    public String private_code;
    private Gson gson;

    public static mainUser singleton(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt){
        if(instance == null){
            instance = new mainUser(label, lat, lon, updatedAt);
        }
        return instance;
    }
    public static mainUser singleton(){
        return instance;
    }



    public static boolean exists(){
        return instance != null;
    }

    public mainUser(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt) {
        super(label, lat, lon, updatedAt);
        this.private_code ="private_"+this.public_code;
        gson = new GsonBuilder().setExclusionStrategies(new JsonExcPut()).setPrettyPrinting().create();
    }

    @Override
    public String toJSON() {
        return new Gson().toJson(this);
    }
}

// Excluding: public_code, updatedAt
class JsonExcPut implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getName().equals("public_code") ||f.getName().equals("updatedAt"));
    }

}
