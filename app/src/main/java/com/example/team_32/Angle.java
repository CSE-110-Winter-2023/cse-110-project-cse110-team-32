package com.example.team_32;

import android.util.Pair;

public class Angle {

        public static double angleBetweenLocations(Pair<Double, Double> loc1 , Pair<Double, Double>loc2, double angleWithNorth) {
            // Convert the latitude and longitude of each point to radians
            var lat1 = Math.toRadians(loc1.first);
            var long1 = Math.toRadians(loc1.second);
            var lat2 = Math.toRadians(loc2.first);
            var long2 = Math.toRadians(loc2.second);

            // Calculate the difference between the longitudes of the two points
            double deltaLong = long2 - long1;

            double X = Math.cos(lat2) * Math.sin(deltaLong);
            double Y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);

            double bearingAngle = Math.atan2( X, Y );

            // Convert the bearing angle from radians to degrees
            bearingAngle = Math.toDegrees(bearingAngle);

            // Calculate the angle between me and the other location
            double finalbearing = (bearingAngle - angleWithNorth + 360) % 360;

            return finalbearing;
        }
}
