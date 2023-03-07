package com.example.team_32;

import androidx.annotation.NonNull;

public class mainUser extends User{
    public String private_code;

    public mainUser(@NonNull String label, @NonNull float lat, @NonNull float lon, long updatedAt) {
        super(label, lat, lon, updatedAt);
        this.private_code ="private_"+this.public_code;
    }
}
