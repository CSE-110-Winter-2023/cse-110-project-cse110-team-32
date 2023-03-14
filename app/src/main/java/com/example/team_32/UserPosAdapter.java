package com.example.team_32;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // getting the position of this friend
        User usr = users.get(position);
        android.util.Pair<Double, Double> mainUsrLoc = new Pair<>((double)mainuser.latitude, (double)mainuser.longitude);
        android.util.Pair<Double, Double> usrLoc = new Pair<>((double)usr.latitude, (double)usr.longitude);
        float[] vector = Utilities.getRelativeVector(mainUsrLoc, usrLoc);
        vector[1] = -vector[1];
        float x =  vector[0], y = vector[1];
        float dist = Utilities.distanceInMiles(vector);
        float len = Utilities.len(vector);
        Log.i("Pos2",  usr.label +" : " + x + ", "+ y  + "dist:"+ Utilities.distanceInMiles(vector)+ "\n" +usr.toJSON());
        View itemView = convertView;

        // based on the distance, populate the correct layOut
        if (dist > zoomLvls[maxZoomIdx]){
            //displayed on the outer ring as a dot only.
            if (itemView == null) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.outer_user_item, parent, false);
            }
            float xOffset = (float) ((parent.getWidth()- itemView.getWidth())/2);
            float yOffset = (float) ((parent.getHeight()-itemView.getHeight())/2);
            Log.i("Pos2",  usr.label + "dist: outerRing" + x + ", " + y + " is : "+dist);
            x /= len;
            y /= len;
            x*= 300;
            y*= 300;
            Log.i("Pos2",  usr.label + "dist: outerRing" + x + ", " + y);
            itemView.setX(xOffset + x);
            itemView.setY(yOffset + y);
        }else {
            if (itemView == null) {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.outer_user_item, parent, false);
            }
//            TextView label = itemView.findViewById(R.id.user_label);
//            label.setText((usr.label));
            x*= 30;
            y*= 30;

            float xOffset = (float) ((parent.getWidth() - itemView.getWidth()) / 2);
            float yOffset = (float) ((parent.getHeight() - itemView.getHeight()) / 2);
            if ((usr.public_code+"_private2").equals(mainuser.private_code)){
                Log.i("Pos2", "getView: "+ usr.label);
                itemView.setX(0);
                itemView.setY(yOffset);
            }
            else {
//                Log.i("Pos2", usr.label + " : inner" + mainuser.public_code);
                itemView.setX(xOffset + x);
                itemView.setY(yOffset + y);
            }
        }
        return itemView;
    }
    @Override
    public int getCount(){
        if (this.users == null)
            return 0;
        return this.users.size();
    }

}
