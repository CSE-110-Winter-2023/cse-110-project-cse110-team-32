package com.example.team_32;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import java.util.List;


public class UserViewModel extends AndroidViewModel {

    private LiveData<List<User>> users;
    private final UserDao userDao;
    private final UserRepo userRepo;
    private mainUser mainuser;

    public UserViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        UserDatabase db = UserDatabase.provide(context);
        userDao = db.getDao();
        userRepo = new UserRepo(userDao, UserAPI.provide());
    }

    public void loadMainUser(String public_code) {
        if (!userRepo.existsLocal(public_code)) {
            this.mainuser = mainUser.singleton();
            updateMain();
        }else {
            this.mainuser = mainUser.fromUser(userDao.getMain(public_code));
        }
    }

    public void updateMain(android.util.Pair<Double, Double> loc) {
        if (mainuser != null){
            mainuser.updateLoc(loc.first, loc.second);
            updateMain();
        }
    }
    public void updateMain() {
        if (mainuser != null){
            userRepo.upsertLocal(mainuser);
            userRepo.upsertRemote(mainuser);
        }
    }

    public LiveData<User> getUser(String code, LifecycleOwner lifeOwner){
        userRepo.getSynced(code);
        if (!userDao.exists(code)){
            var temp = userRepo.getRemote(code);
            temp.observe(lifeOwner, userEntity -> {
                // ...stop observing.
                if (userEntity != null) {
                    temp.removeObservers(lifeOwner);
                    userDao.upsert(userEntity);
                }
                else{
                    Log.i("DD", "getUser: null1!!");
                }
            });
        }
        return userRepo.getSynced(code);
    }

    public String getMainUserCode (){
        if (mainuser == null)
            mainuser = mainUser.singleton();
        return mainuser.public_code;
    }
    public void reSyncAll(){
        var data =getUsers();
        Utilities.observeOnce(data, usrs ->{
            for (var usr: usrs){
                if (!(usr.public_code+"_private2").equals(mainuser.private_code)){
                    userRepo.getSynced(usr.public_code);
                }
            }
        });
    }

    public LiveData<List<User>> getUsers() {
        return userRepo.getAllLocal();
    }
}
