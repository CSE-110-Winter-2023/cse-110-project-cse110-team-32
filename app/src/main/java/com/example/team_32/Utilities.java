package com.example.team_32;

import android.util.Pair;

public class Utilities {

        public static double angleBetweenLocations(Pair<Double, Double> loc1 , Pair<Double, Double>loc2, double angleWithNorth) {
            // Convert the latitude and longitude of each point to radians
            var lat1 = Math.toRadians(loc1.first);
            var lon1 = Math.toRadians(loc1.second);
            var lat2 = Math.toRadians(loc2.first);
            var lon2 = Math.toRadians(loc2.second);

            // Calculate the difference between the longitudes of the two points
            double deltaLon = lon2 - lon1;

            // Calculate the central angle between the two points
            double centralAngle = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                    Math.cos(lat1) * Math.cos(lat2) * Math.cos(deltaLon));

            // Calculate the initial bearing from point 1 to point 2
            double initialBearing = Math.atan2(Math.sin(deltaLon) * Math.cos(lat2),
                    Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon));

            // Convert the bearing from radians to degrees
            initialBearing = Math.toDegrees(initialBearing);

            // Calculate the angle between me and the other location
            double angle = (initialBearing - angleWithNorth + 360) % 360;

            return angle;
        }
}
