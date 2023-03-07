package com.example.team_32;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserRepo {
    private final UserDao dao;
    private final UserAPI api;
    private final HashMap<String, LiveData<User>>userCache;

    private ScheduledFuture<?> poller;

    public UserRepo(UserDao dao, UserAPI api){
        this.dao = dao;
        this.api =api;
        this.userCache = new HashMap<>();
    }
    // Sync Methods
    // =============


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
        dao.upsert(user);
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
            String noteBody = api.getNote(public_code);
            if (noteBody.contains(public_code)){
                User tempNote = User.fromJSON(noteBody);
                user.postValue(tempNote);
            }
        }, 3, 3, TimeUnit.SECONDS);

        userCache.put(public_code, user);
        return user;
    }


    public void upsertRemote(User note) {
        throw new UnsupportedOperationException("Not implemented yet");
//        ExecutorService ex = Executors.newSingleThreadExecutor();
//        String title = note.title;
//        String json = note.toJSON();
//        Log.i("upsertRemote:", json);
//        ex.execute(() -> {
//            noteAPI.putNote(title, json);
//        });
    }


}
