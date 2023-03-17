package com.example.team_32;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

public class RingAdapter extends ArrayAdapter<User> {
    private static RingAdapter instance;
    private mainUser mainuser;
    private final Context mContext;
    private List<User> users;

    private int zoomState = 1;

    private int maxZoomIdx;
    private final float[] zoomLvls = {1.0F, 10.0F, 500.0F};

    public static RingAdapter singleton(@NonNull Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new RingAdapter(context);
        return instance;
    }

    public static RingAdapter singleton() {
        if (instance != null) {
            return instance;
        }
        return null;
    }


    public RingAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
        this.mainuser = mainUser.singleton();
        this.maxZoomIdx = 1;
    }

    public void setZoomState(int zoomState) {
        System.out.println("OverLap"+ "setZoomState: " + zoomState);
        Log.i("OverLap", "setZoomState: " + zoomState);
        this.zoomState = zoomState;
        notifyDataSetChanged();
    }

    public void setUsers(List<User> usrs) {
        if (mainuser == null) {
            if (!mainUser.exists()) {
                return;
            }
            mainuser = mainUser.singleton();
        }
        List<User> tempUsrs = new ArrayList<>();
        //removing mainUser
        for (User usr : usrs) {
            if ((usr.public_code + "_private2").equals(mainuser.private_code)) {
                continue;
            }
            tempUsrs.add(usr);
        }
        this.users = tempUsrs;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mainuser == null) {
            mainuser = mainUser.singleton();
            if (mainuser == null) return null;
        }
        // getting the position of this friend
        User usr = users.get(position);
        if (usr == null) return null;
        // finding relative vector
        android.util.Pair<Double, Double> mainUsrLoc = new Pair<>((double) mainuser.latitude, (double) mainuser.longitude);
        android.util.Pair<Double, Double> usrLoc = new Pair<>((double) usr.latitude, (double) usr.longitude);
        float[] vector = Utilities.getRelativeVector(mainUsrLoc, usrLoc);
        vector[1] = -vector[1];
        float x = vector[0], y = vector[1];
        // getting distance and normalize vector
        float dist = Utilities.distanceInMiles(vector);
        float len = Utilities.lenOfVector(vector);
        x /= len;
        y /= len;
        System.out.println("Place: " + usr.label + "dist: " + dist + " state: " +zoomState+"\n");
        Log.i("Pos2", usr.label + " : " + x + ", " + y + "dist:" + dist + "\n" + usr.toJSON());
        // Create the view if needed
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.uesr_item, parent, false);
        }
        //
        TextView label = itemView.findViewById(R.id.usr_label);
        label.setText(usr.label);
        ImageView dot = itemView.findViewById(R.id.usr_dot);

        float xOffset = (float) ((parent.getWidth() - itemView.getWidth()) / 2);
        float yOffset = (float) ((parent.getHeight() - itemView.getHeight()) / 2);

        Log.i("ZOOM", usr.label + " in " + dist + "\n" + usr.toJSON());

        // based on the distance and zoom state populate the correct layOut
        // state 0: only the 1 mile rings
        // ring radius = 950/2
        if (zoomState == 0) {
            Log.i("ZOOM", "State 0");
            float rad = 950F / 2F;
            float radOff= 50F;
            if (dist > zoomLvls[0]) {
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                //displayed on edge of the ring as a dot only.
                x *= rad;
                y *= rad;
            } else {
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                var tempX = radOff * x;
                var tempY = radOff * y;
                x *= (rad -radOff) * (dist / 1.0);
                y *= (rad- radOff) * (dist / 1.0);
                x += tempX;
                y+= tempY;
            }
        } else if (zoomState == 1) {
            // state 1: 1,10 miles rings
            // outer ring radius = 900
            // inner ring radius = 450
            Log.i("ZOOM", "State 1");
            float radOuter = 900.0F / 2.0F;
            float radInner = 450.0F / 2.0F;
            float radOff = 50F;
            if (dist > zoomLvls[1]) {
                Log.i("ZOOM", "State 1: Outer");
                //displayed on edge of the outer ring as a dot only.
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                x *= radOuter;
                y *= radOuter;
            } else {
                Log.i("ZOOM", "State 1: Inner");
                // displayed within the rings
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                // between 0-1
                if (dist < zoomLvls[0]) {
                    Log.i("ZOOM", "State 1: Inner 0-1");
                    var tempX = radOff * x;
                    var tempY = radOff * y;
                    x *= (radInner -radOff) * (dist / 1.0);
                    y *= (radInner- radOff) * (dist / 1.0);
                    x += tempX;
                    y+= tempY;
                } else {
                    // move it to the edge of the inner ring
                    var tempX = radInner * x;
                    var tempY = radInner * y;
                    x *= ((radOuter - radInner) * dist / 10.0F);
                    y *= ((radOuter - radInner) * dist / 10.0F);
                    x += tempX;
                    y += tempY;
                    Log.i("ZOOM", "State 1: Inner 1-10 with :" + x + " , " + y);

                }
            }
        } else if (zoomState == 2) {
            System.out.println("State 2");
            // state 2: 1, 10, 500 miles rings
            // outRing: 950/2, inRing = 750/2, innerRing = 350
            Log.i("ZOOM", "State 2");
            float radOuter = 950.0F / 2.0F;
            float radIn = 750.0F / 2.0F;
            float radInner = 350.0F / 2.0F;
            float radOff = 50F;
            if (dist > zoomLvls[2]) {
                System.out.println("ring ?");
                Log.i("ZOOM", "State 2: Outer");
                //displayed on edge of the outer ring as a dot only.
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                x *= radOuter;
                y *= radOuter;
            } else {
                System.out.println("none ring");
                Log.i("ZOOM", "State 1: Inner");
                // displayed within the rings
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                // between 0-1
                if (dist < zoomLvls[0]) {
                    Log.i("ZOOM", "State 2: Inner 0-1");
                    var tempX = radOff * x;
                    var tempY = radOff * y;
                    x *= (radInner -radOff) * (dist / 1.0);
                    y *= (radInner- radOff) * (dist / 1.0);
                    x += tempX;
                    y+= tempY;
                } else if (dist < zoomLvls[1]) {
                    Log.i("ZOOM", "State 2: Inner 1-10");
                    // between 1-10
                    // move it to the edge of the inner ring
                    var tempX = radInner * x;
                    var tempY = radInner * y;
                    x *= ((radIn - radInner) * dist / 10.0F);
                    y *= ((radIn - radInner) * dist / 10.0F);
                    x += tempX;
                    y += tempY;
                } else {
                    //between 10-500
                    var tempX = radIn * x;
                    var tempY = radIn * y;
                    x *= ((radOuter - radIn) * dist / 500.0F);
                    y *= ((radOuter - radIn) * dist / 500.0F);
                    x += tempX;
                    y += tempY;
                }
            }
        } else if (zoomState == 3) {
            System.out.println("state 3");
            float radOuterer =  1000.0F / 2.0F;
            float radOuter = 850.0F / 2.0F;
            float radIn = 650.0F / 2.0F;
            float radInner = 350.0F / 2.0F;
            float radOff = 50F;
            if (dist > zoomLvls[2]) {
                System.out.println("showen ?");
                //500+
                Log.i("ZOOM", "State 2: Outer");
                //displayed on edge of the outer ring as a dot only.
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                x *= (radOuter + radOuterer)/2;
                y *= (radOuter + radOuterer)/2;
            } else {
                Log.i("ZOOM", "State 1: Inner");
                // displayed within the rings
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                // between 0-1
                if (dist < zoomLvls[0]) {
                    Log.i("ZOOM", "State 2: Inner 0-1");
                    var tempX = radOff * x;
                    var tempY = radOff * y;
                    x *= (radInner -radOff) * (dist / 1.0);
                    y *= (radInner- radOff) * (dist / 1.0);
                    x += tempX;
                    y+= tempY;
                } else if (dist < zoomLvls[1]) {
                    Log.i("ZOOM", "State 2: Inner 1-10");
                    // between 1-10
                    // move it to the edge of the inner ring
                    var tempX = radInner * x;
                    var tempY = radInner * y;
                    x *= ((radIn - radInner) * dist / 10.0F);
                    y *= ((radIn - radInner) * dist / 10.0F);
                    x += tempX;
                    y += tempY;
                } else {
                    //between 10-500
                    var tempX = radIn * x;
                    var tempY = radIn * y;
                    x *= ((radOuter - radIn) * dist / 500.0F);
                    y *= ((radOuter - radIn) * dist / 500.0F);
                    x += tempX;
                    y += tempY;
                }
            }
        }
        Log.i("OverLap", "Done ?");
        Log.i("OverLap", "=====================" + usr.label + "===============================");
        for (int i = 0; i < position; i++) {
            User otherUser = users.get(i);
            View otherItemView = parent.getChildAt(i);
            Log.i("OverLap", "check with:" + otherUser.label);
            if (otherItemView != null) {
                float otherX = otherItemView.getX();
                float otherY = otherItemView.getY();
                float deltaX = Math.abs(otherX - (xOffset + x));
                float deltaY = Math.abs(otherY - (yOffset + y));

                float minDistance = 80; // You can adjust this value to your needs
                boolean isOverlapping = deltaX < minDistance && deltaY < minDistance;

                if (isOverlapping) {
                    Log.i("Rev", "getView: "+ dist + " state : " +zoomState);
                    Log.i("OverLap", usr.toJSON() + "\n with \n" + otherUser.toJSON());
                    // Truncate labels that overlap
                    String truncatedLabel = usr.label.substring(0, Math.min(usr.label.length(), 5)) + "...";
                    label.setText(truncatedLabel);

                    // Stack labels that are in the same orientation and circle
                    if (zoomState == 0 && dist <= zoomLvls[0]) {
                        if (i % 2 == 0) {
                            //going down
                            x += minDistance / 2;
                            y += minDistance / 2;
                                if (x + xOffset <= (50.0F / 2.0F)|| y + yOffset <= (50.0F / 2.0F)){
                                    Log.i("Reverse", "-- ");
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                        } else {
                            //going up
                            x -= minDistance / 2;
                            y -= minDistance / 2;
                            if (x + xOffset >= (950.0F / 2.0F)|| y + yOffset >= (950.0F / 2.0F)){
                                //push it down
                                x += minDistance;
                                y += minDistance;
                            }
                        }
                    } else if (zoomState == 1 && dist <= zoomLvls[1]) {
                        minDistance *= 0.8;
                        if (i % 2 == 0) {
                            //going down
                            x += minDistance / 2;
                            y += minDistance / 2;
                            Log.i("Reverse", "dist: " + dist);
                            if (dist > 1){
                                if (x + xOffset <= (450.0F / 2.0F)|| y + yOffset <= (450.0F / 2.0F)){
                                    Log.i("Reverse", "-- ");
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }
                        } else {
                            //going up
                            x -= minDistance / 2;
                            y -= minDistance / 2;
                            if (dist <= 1){
                                if (x + xOffset >= (450.0F / 2.0F)|| y + yOffset >= (450.0F / 2.0F)){
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }else {
                                if (x + xOffset <= (450.0F / 2.0F)|| y + yOffset <= (450.0F / 2.0F)){
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }
                        }
                    } else if (zoomState == 2 && dist <= zoomLvls[2]) {
                        if (i % 2 == 0) {
                            //going down
                            x += minDistance / 2;
                            y += minDistance / 2;
                            if (dist > 10){
                                if (x + xOffset <= (750.0F / 2.0F)|| y + yOffset <= (750.0F / 2.0F)){
                                    //go up
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }else if (dist > 1){
                                if (x + xOffset <= (350.0F / 2.0F)|| y + yOffset <= (350.0F / 2.0F)){
                                    //go up
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }
                        } else {
                            //going up
                            x -= minDistance / 2;
                            y -= minDistance / 2;
                            if (dist > 10){
                                if (x + xOffset >= (950.0F / 2.0F)|| y + yOffset >= (950.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }else if (dist > 1){
                                if (x + xOffset >= (750.0F / 2.0F)|| y + yOffset >= (750.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }else {
                                if (x + xOffset >= (350.0F / 2.0F)|| y + yOffset >= (350.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }
                        }
                    } else if (zoomState == 3) {
                        minDistance = 30;
                        if (i % 2 == 0) {
                            //going down
                            x += minDistance / 2;
                            y += minDistance / 2;
                            if (dist > 500){
                                if (x + xOffset <= (850.0F / 2.0F)|| y + yOffset <= (850.0F / 2.0F)){
                                    //go up
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }
                            else if (dist > 10){
                                if (x + xOffset <= (650.0F / 2.0F)|| y + yOffset <= (650.0F / 2.0F)){
                                    //go up
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }else if (dist > 1){
                                if (x + xOffset <= (350.0F / 2.0F)|| y + yOffset <= (350.0F / 2.0F)){
                                    //go up
                                    x -= minDistance;
                                    y -= minDistance;
                                }
                            }
                        } else {
                            //going up
                            x -= minDistance / 2;
                            y -= minDistance / 2;
                            if (dist > 500){
                                if (x + xOffset >= (1000.0F / 2.0F)|| y + yOffset >= (1000.0F / 2.0F)){
                                    //go up
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }
                            else if (dist > 10){
                                if (x + xOffset >= (950.0F / 2.0F)|| y + yOffset >= (950.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }else if (dist > 1){
                                if (x + xOffset >= (750.0F / 2.0F)|| y + yOffset >= (750.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }else {
                                if (x + xOffset >= (350.0F / 2.0F)|| y + yOffset >= (350.0F / 2.0F)){
                                    //go down
                                    x += minDistance;
                                    y += minDistance;
                                }
                            }
                        }
                    }
                }
            }
        }
        itemView.setX(xOffset + x);
        itemView.setY(yOffset + y);
        return itemView;
    }

    @Override
    public int getCount() {
        if (this.users == null) return 0;
        return this.users.size();
    }
    @VisibleForTesting
    public void resetMain (){
        mainuser = mainUser.singleton();
    }
    @VisibleForTesting
    public void getMainJson (){
        System.out.println(mainuser.toJSON());
    }

}
