package com.example.team_32;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Room;
import androidx.room.RoomDatabase;

public abstract class UserDatabase extends RoomDatabase {
    private volatile static UserDatabase instance = null;
    public abstract UserDao getDao();

    public synchronized static UserDatabase provide(Context context) {
        if (instance == null) {
            instance = UserDatabase.make(context);
        }
        return instance;
    }

    private static UserDatabase make(Context context) {
        return Room.databaseBuilder(context, UserDatabase.class, "user_app.db")
                .allowMainThreadQueries()
                .build();
    }

    @VisibleForTesting
    public static void inject(UserDatabase testDatabase) {
        if (instance != null ) {
            instance.close();
        }
        instance = testDatabase;
    }
}
