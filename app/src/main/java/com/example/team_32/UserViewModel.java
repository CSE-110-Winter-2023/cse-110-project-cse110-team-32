package com.example.team_32;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private LiveData<List<User>> users;
//    private final UserDao userDao;

    public UserViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        UserDatabase db = UserDatabase.provide(context);
//        userDao = db.getDao();
    }
}
