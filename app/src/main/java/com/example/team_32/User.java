package com.example.team_32;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.time.Instant;

class TimestampAdapter extends TypeAdapter<Long> {
    @Override
    public void write(JsonWriter out, Long value) throws java.io.IOException {
        var instant = Instant.ofEpochSecond(value);
        out.value(instant.toString());
    }

    @Override
    public Long read(JsonReader in) throws java.io.IOException {
        var instant = Instant.parse(in.nextString());
        return instant.getEpochSecond();
    }
}

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @SerializedName("public_code")
    @NonNull
    public String public_code;

    @SerializedName("label")
    @NonNull
    public String label;

    @SerializedName("latitude")
    @NonNull
    public float lat;

    @SerializedName("longitude")
    @NonNull
    public float lon;

    @JsonAdapter(TimestampAdapter.class)
    @SerializedName(value = "updated_at", alternate = "updatedAt")
    public long updatedAt = 0;

    @Ignore
    public User(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt){
        this.label = label;
        this.lat =  lat;
        this.lon =  lon;
        this.updatedAt = updatedAt;
        var num = Math.floor(Math.random() *(198412 - 123 + 1) + 123);
        String temp = label.replace(" ","-") +"-"+ Double.toString(num);
        this.public_code = temp;
    }
    public static User fromJSON(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
