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
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserPosAdapter extends ArrayAdapter<User> {
    private mainUser mainuser;
    private final Context mContext;
    private List<User> users;
    private  float offset = 50;
    private int maxZoomIdx;
    private final float[] zoomLvls  = {1.0F, 10.0F, 500.0F};

    public UserPosAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
        this.mainuser= mainUser.singleton();
        this.maxZoomIdx =1;
    }

    public void setUsers(List<User> usrs){
            this.users = usrs;
            Log.i("getView", "calling a change" + this.users.size());
            notifyDataSetChanged();
    }

    // TODO: Change the dots to names in the inner rings
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
        android.util.Pair<Double, Double> mainUsrLoc = new Pair<>((double)mainuser.latitude, (double)mainuser.longitude);
        android.util.Pair<Double, Double> usrLoc = new Pair<>((double)usr.latitude, (double)usr.longitude);
        float[] vector = Utilities.getRelativeVector(mainUsrLoc, usrLoc);
        vector[1] = -vector[1];
        float x =  vector[0], y = vector[1];
        float dist = Utilities.distanceInMiles(vector);
        float len = Utilities.len(vector);
        Log.i("Pos2",  usr.label +" : " + x + ", "+ y  + "dist:"+ dist+ "\n" +usr.toJSON());
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.uesr_item, parent, false);
        }
        TextView label = itemView.findViewById(R.id.usr_label);
        ImageView dot = itemView.findViewById(R.id.usr_dot);
        Log.i("POS2", label + " or " + usr.label);
        label.setText(usr.label);

        float xOffset = (float) ((parent.getWidth()- itemView.getWidth())/2);
        float yOffset = (float) ((parent.getHeight()-itemView.getHeight())/2);

        //normalize
        x /= len;
        y /= len;
        // based on the distance, populate the correct layOut

        if (dist > zoomLvls[maxZoomIdx]){
            label.setVisibility(View.GONE);
            dot.setVisibility(View.VISIBLE);
            //displayed on the outer ring as a dot only.
            Log.i("Pos2",  usr.label + "dist: outerRing" + x + ", " + y + " is : "+dist);

            x*= 450;
            y*= 450;
            Log.i("Pos2",  usr.label + "dist: outerRing" + x + ", " + y);
            itemView.setX(xOffset + x);
            itemView.setY(yOffset + y);
        }else {
            label.setVisibility(View.VISIBLE);
            dot.setVisibility(View.GONE);
            if (dist < 1.0F){
                Log.i("Pos2", "1: " + dist );
//            TextView label = itemView.findViewById(R.id.user_label);
//            label.setText((usr.label));
                x *= ((450.0/2.0) * (dist/1.0));
                y *= ((450.0/2.0)* (dist/1.0));
            }else {
                Log.i("Pos2", "10: "  + dist);
//            TextView label = itemView.findViewById(R.id.user_label);
//            label.setText((usr.label));
                //moving it to the edge of the 1mile ring
                x *= (450.0/2.0 * ((dist/10.0) +1.0));
                y *= (450.0/2.0* ((dist/10.0) +1.0));
            }

            if ((usr.public_code + "_private2").equals(mainuser.private_code)) {
                Log.i("Pos2", "getView: " + usr.label);
                itemView.setX(0);
                itemView.setY(yOffset);
            } else {
                Log.i("Pos2", usr.label + " : inner" + (x) + ", "+(y));
                itemView.setX(xOffset + x);
                itemView.setY(yOffset + y);
            }
        }
        Log.i("Pos2", "----------------------------");
        return itemView;
    }
    @Override
    public int getCount(){
        if (this.users == null)
            return 0;
        return this.users.size();
    }

}