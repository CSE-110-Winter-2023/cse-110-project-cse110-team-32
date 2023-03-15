package com.example.team_32;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
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
        Log.i("DD!!", "getSynced: getting Synced ? " + public_code);
        var m_user = new MutableLiveData<User>();
        Observer<User> updateFromRemote = theirUser -> {
            Log.i("DD!!", "getSynced: upsert locally ? started");
            var ourUser = m_user.getValue();
            if (theirUser == null){
                Log.i("DD!!", "getSynced: upsert locally ? failed ?");
                return;} // do nothing
            if (ourUser == null || ourUser.updatedAt < theirUser.updatedAt) {
                Log.i("DD!!", "getSynced: upsert locally ? ");
                upsertLocal(theirUser);
            }
        };

        getLocal(public_code).observeForever(m_user::postValue);
       if (api != null) getRemote(public_code).observeForever(updateFromRemote);
        return m_user;
    }

    // Local Methods
    // =============
    public LiveData<User> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public LiveData<List<User>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(User user) {
        user.updatedAt = System.currentTimeMillis()/1000;
        Log.i("PRE11", "upsertLocal: " + dao.upsert(user));
    }
    @VisibleForTesting
    public void upsertAllLocal(List<User> usrs){
        if (usrs == null)
            return;
        for (var usr: usrs) {
            upsertLocal(usr);
        }
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
        if (api == null)
            return null;
        //already having the user
        if (userCache.containsKey(public_code))
            return userCache.get(public_code);

        MutableLiveData<User> user = new MutableLiveData<>();
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(() -> {
            String userInfo = api.getUser(public_code);
            if (userInfo.contains(public_code)){
                User tempNote = User.fromJSON(userInfo);
                user.postValue(tempNote);
            }});

        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
        exe.scheduleAtFixedRate(() -> {
            String userInfo = api.getUser(public_code);
            if (userInfo.contains(public_code)){
                User tempNote = User.fromJSON(userInfo);
                user.postValue(tempNote);
                Log.i("POSTED Value: ", tempNote.toJSON());
            }}, 3, 3, TimeUnit.SECONDS);

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
