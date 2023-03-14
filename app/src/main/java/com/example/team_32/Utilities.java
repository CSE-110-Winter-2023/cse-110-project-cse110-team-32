package com.example.team_32;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utilities {
    public static Optional<Double> parseDouble(String str) {
        try {
            double number = Double.parseDouble(str);
            return Optional.of(number);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    public static final double R = 6371.0; // radius of the Earth in km
    public static final float km_miles = (float) 0.621371;


    public static double[] toCartesian(double latitude, double longitude) {
        double[] xy = new double[2];
        xy[0] = R * Math.toRadians(longitude);
        xy[1] = R * Math.log(Math.tan(Math.PI / 4 + Math.toRadians(latitude) / 2));
        return xy;
    }

    public static float[] getRelativeVector(android.util.Pair<Double, Double> loc,
                                            android.util.Pair<Double, Double> loc2) {
        double latitude1 = loc.first;
        double longitude1 = loc.second;
        double latitude2 = loc2.first;
        double longitude2 = loc2.second;
        double[] xy1 = toCartesian(latitude1, longitude1);
        double[] xy2 = toCartesian(latitude2, longitude2);
        float dx = (float) (xy2[0] - xy1[0]);
        float dy = (float)(xy2[1] - xy1[1]);
//        double x = (longitude2 - longitude1) * Math.cos((latitude1 + latitude2) / 2);
//        double y = latitude2 - latitude1;
//        float[] result = {(float) x, (float) y};
//        return result;
        return new float[]{dx, dy};
    }

    public static float distanceInMiles(float[] vector){
        return ((float) Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]) *km_miles);
    }

    public static <T> void observeOnce(LiveData<T> liveData, Observer<T> observer) {
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t);
                liveData.removeObserver(this);
            }
        });
    }

}
