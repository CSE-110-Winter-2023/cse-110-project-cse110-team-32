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

    public UserPosAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
        this.mainuser= mainUser.singleton();
    }

    public void setUsers(List<User> usrs){
            this.users = usrs;
            Log.i("getView", "calling a change" + this.users.size());
            notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.e("UserPosAdapter", "users list is null or empty");
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.uesr_item, parent, false);
        }
        TextView label = itemView.findViewById(R.id.user_label);
        ConstraintLayout con = itemView.findViewById(R.id.constID);
        User usr = users.get(position);
        label.setText((usr.label));

        float xOffset = (float) ((parent.getWidth()- 120.0)/2);
        float yOffset = (float) ((parent.getHeight()-25.0)/2);
        Log.i("Offsets", yOffset+"getView: "+ xOffset);

//        Log.i("Making a view", "getView: " + position + "for \n"+ usr.toJSON());

        double cor = Angle.angleBetweenLocations(new Pair<Double,Double>((double) usr.latitude, (double) usr.longitude), new Pair<Double,Double>((double) mainuser.latitude, (double) mainuser.longitude), 0);
        float x =  usr.longitude - mainuser.longitude, y = usr.latitude - mainuser.latitude;
        Log.i("Pos",  usr.label +" : " + x + ", "+ y + "\n"+usr.toJSON());

        itemView.setX(xOffset + x*-1);
        itemView.setY(yOffset + y*-1);
        return itemView;
    }
    @Override
    public int getCount(){
        if (this.users == null)
            return 0;
        return this.users.size();
    }

}
