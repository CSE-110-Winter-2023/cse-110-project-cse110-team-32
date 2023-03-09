package com.example.team_32;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import java.util.HashMap;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserRepo {
    private final UserDao dao;
    private final UserAPI api;
    private final HashMap<String, LiveData<User>>userCache;

    public UserRepo(UserDao dao, UserAPI api){
        this.dao = dao;
        this.api =api;
        this.userCache = new HashMap<>();
    }
    // Sync Methods
    // =============
    public LiveData<User> getSynced(String public_code) {
        var user = new MediatorLiveData<User>();

        Observer<User> updateFromRemote = theirUser -> {
            var ourUser = user.getValue();
            if (theirUser == null) return; // do nothing
            if (ourUser == null || ourUser.updatedAt < theirUser.updatedAt) {
                upsertLocal(theirUser);
            }
        };

        // If we get a local update, pass it on.
        user.addSource(getLocal(public_code), user::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        user.addSource(getRemote(public_code), updateFromRemote);

        return user;
    }

    // Local Methods
    // =============
    public LiveData<User> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public User getLocalMain(String public_code) {
        return dao.getMain(public_code);
    }

    public LiveData<List<User>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(User user) {
        user.updatedAt = System.currentTimeMillis()/1000;
        Log.i("PRE11", "upsertLocal: " + dao.upsert(user));
    }

    public void deleteLocal(User user) {
        dao.delete(user);
    }
    public boolean existsLocal(String public_code) {
        return dao.exists(public_code);
    }

    // Remote Methods
    // ==============
    // get the data for a user from the server
    public LiveData<User> getRemote(String public_code) {
        Log.i("GET Remote User", "Started");
        //already having the user
        if (userCache.containsKey(public_code))
            return userCache.get(public_code);

        MutableLiveData<User> user = new MutableLiveData<>();
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(() -> {
            String userInfo = api.getUser(public_code);
            if (userInfo.contains(public_code)){
                Log.i("GET Remote User", userInfo);
                User tempUser = User.fromJSON(userInfo);
                Log.i("GET Remote User", "Data" + tempUser.label);
                user.postValue(tempUser);
            }else {
                user.postValue(null);
            }
        });
        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
        exe.scheduleAtFixedRate(() -> {
            String userInfo = api.getUser(public_code);
            if (userInfo.contains(public_code)){
                Log.i("GET Remote User", userInfo);
                User tempUser = User.fromJSON(userInfo);
                Log.i("GET Remote User", "label: " + tempUser.label);
                user.postValue(tempUser);
            }
        }, 0, 3, TimeUnit.SECONDS);

        userCache.put(public_code, user);
        return user;
    }


    public void upsertRemote(User mainUser) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        String public_code = mainUser.public_code;
        String json = mainUser.toJSON();
        Log.i("upsertRemote:", json);
        ex.execute(() -> {
            api.putUser(public_code, json);
        });
    }
}
