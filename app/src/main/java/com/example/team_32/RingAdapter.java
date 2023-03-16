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

import java.util.ArrayList;
import java.util.List;

public class RingAdapter extends ArrayAdapter<User> {
    private mainUser mainuser;
    private final Context mContext;
    private List<User> users;

    private int zoomState = 1;

    private int maxZoomIdx;
    private final float[] zoomLvls  = {1.0F, 10.0F, 500.0F};

    public RingAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
        this.mainuser= mainUser.singleton();
        this.maxZoomIdx =1;
    }
    public void setZoomState(int zoomState){
        Log.i("ZoomChanged", "setZoomState: " + zoomState);
        this.zoomState = zoomState;
        notifyDataSetChanged();
    }

    public void setUsers(List<User> usrs){
        if (mainuser == null) {
            if (!mainUser.exists()){
                return;
            }
            mainuser = mainUser.singleton();
        }
        List<User> tempUsrs = new ArrayList<>();
        for (User usr : usrs){
            if ((usr.public_code + "_private2").equals(mainuser.private_code)) {
                continue;
            }
            tempUsrs.add(usr);
        }
        this.users = tempUsrs;
//        Log.i("getView", "calling a change" + this.users.size());
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mainuser == null){
            mainuser= mainUser.singleton();
            if (mainuser == null)
                return null;
        }
        // getting the position of this friend
        User usr = users.get(position);
        if (usr == null)
            return null;
        // finding relative vector
        android.util.Pair<Double, Double> mainUsrLoc = new Pair<>((double)mainuser.latitude, (double)mainuser.longitude);
        android.util.Pair<Double, Double> usrLoc = new Pair<>((double)usr.latitude, (double)usr.longitude);
        float[] vector = Utilities.getRelativeVector(mainUsrLoc, usrLoc);
        vector[1] = -vector[1];
        float x =  vector[0], y = vector[1];
        // getting distance and normalize vector
        float dist = Utilities.distanceInMiles(vector);
        float len = Utilities.lenOfVector(vector);
        x /= len;
        y /= len;

        Log.i("Pos2",  usr.label +" : " + x + ", "+ y  + "dist:"+ dist+ "\n" +usr.toJSON());
        // Create the view if it does not exsist
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.uesr_item, parent, false);
        }
        //
        TextView label = itemView.findViewById(R.id.usr_label);
        label.setText(usr.label);
        ImageView dot = itemView.findViewById(R.id.usr_dot);

        float xOffset = (float) ((parent.getWidth()- itemView.getWidth())/2);
        float yOffset = (float) ((parent.getHeight()-itemView.getHeight())/2);

        Log.i("ZOOM", usr.label + " in " + dist + "\n"+ usr.toJSON());
        // mainUser Case:
        if ((usr.public_code + "_private2").equals(mainuser.private_code)) {
            label.setVisibility(View.GONE);
            dot.setVisibility(View.VISIBLE);
            itemView.setX(0);
            itemView.setY(yOffset);
            return itemView;
        }

        // based on the distance and zoom state populate the correct layOut
        // state 0: only the 1 mile rings
        // ring radius = 950/2
        if (zoomState == 0){
            Log.i("ZOOM", "State 0");
            float rad = 950F/2F;
            if (dist > zoomLvls[0]){
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                //displayed on edge of the ring as a dot only.
                x*= rad;
                y*= rad;
            }else {
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                x*= rad * (dist/1.0);
                y*= rad * (dist/1.0);
            }
            itemView.setX(xOffset+x);
            itemView.setY(yOffset+y);
            return itemView;
        }
        // state 1: 1,10 miles rings
        // outer ring radius = 900
        // inner ring radius = 450
        if (zoomState == 1){
            Log.i("ZOOM", "State 1");
            float radOuter = 900.0F/2.0F;
            float radInner = 450.0F/2.0F;
            if (dist > zoomLvls[1]){
                Log.i("ZOOM", "State 1: Outer");
                //displayed on edge of the outer ring as a dot only.
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                x*= radOuter;
                y*= radOuter;
            }else {
                Log.i("ZOOM", "State 1: Inner");
                // displayed within the rings
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                // between 0-1
                if (dist < zoomLvls[0]){
                    Log.i("ZOOM", "State 1: Inner 0-1");
                    x *= (radInner) * (dist/1.0);
                    y *= (radInner) * (dist/1.0);
                }else {
                    // move it to the edge of the inner ring
                    var tempX =radInner *x;
                    var tempY =radInner *y;
                    x *= ((radOuter - radInner) * dist/10.0F);
                    y *= ((radOuter - radInner) * dist/10.0F);
                    x+= tempX;
                    y+= tempY;
                    Log.i("ZOOM", "State 1: Inner 1-10 with :"  +x + " , " + y);

                }
            }
            itemView.setX(xOffset+x);
            itemView.setY(yOffset+y);
            return itemView;
        }
        // state 2: 1, 10, 500 miles rings
        // outRing: 950/2, inRing = 750/2, innerRing = 350
        if (zoomState == 2){
            Log.i("ZOOM", "State 2");
            float radOuter = 950.0F/2.0F;
            float radIn = 750.0F/2.0F;
            float radInner = 350.0F/2.0F;
            if (dist > zoomLvls[2]){
                Log.i("ZOOM", "State 2: Outer");
                //displayed on edge of the outer ring as a dot only.
                label.setVisibility(View.GONE);
                dot.setVisibility(View.VISIBLE);
                x*= radOuter;
                y*= radOuter;
            }else {
                Log.i("ZOOM", "State 1: Inner");
                // displayed within the rings
                label.setVisibility(View.VISIBLE);
                dot.setVisibility(View.GONE);
                // between 0-1
                if (dist < zoomLvls[0]){
                    Log.i("ZOOM", "State 2: Inner 0-1");
                    x *= (radInner) * (dist/1.0);
                    y *= (radInner) * (dist/1.0);
                }else if (dist < zoomLvls[1]){
                    Log.i("ZOOM", "State 2: Inner 1-10");
                    // between 1-10
                    // move it to the edge of the inner ring
                    var tempX =radInner *x;
                    var tempY =radInner *y;
                    x *= ((radIn - radInner) * dist/10.0F);
                    y *= ((radIn - radInner) * dist/10.0F);
                    x+= tempX;
                    y+= tempY;
                }else {
                    //between 10-500
                    var tempX =radIn *x;
                    var tempY =radIn *y;
                    x *= ((radIn - radIn) * dist/500.0F);
                    y *= ((radIn - radIn) * dist/500.0F);
                    x+= tempX;
                    y+= tempY;
                }
            }
            itemView.setX(xOffset+x);
            itemView.setY(yOffset+y);
            return itemView;
        }
        Log.i("Pos2", "----------------------------");
        return null;
    }
    @Override
    public int getCount(){
        if (this.users == null)
            return 0;
        return this.users.size();
    }

}
