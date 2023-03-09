package com.example.team_32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataBaseTesting {

    private UserDao dao;
    private UserDatabase db;

    @Before
    public void createDB(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase.class).allowMainThreadQueries().build();
        dao = db.getDao();
    }

    @After
    public void closeDB(){
        db.close();
    }

    @Test
    public void testInsert(){
        User item1 = new User("Ali");
        User item2 = new User("Ali2");

        long id1 = dao.upsert(item1);
        long id2 = dao.upsert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet(){
        User item1 = new User( "Ali");
        dao.upsert(item1);
        User item = dao.getMain(item1.public_code);
        assertEquals(item.public_code, item1.public_code);
        assertEquals(item.label,item1.label);
    }

    @Test
    public void testDelete(){
        User item1 = new User( "Ali");
        dao.upsert(item1);
        int itemDeleted = dao.delete(item1);
        assertEquals(1,itemDeleted);
        assertNull(dao.getMain(item1.public_code));
    }

    @Test
    public void testExists (){
        User item1 = new User( "Ali");
        dao.upsert(item1);
        assertTrue(dao.exists(item1.public_code));
    }
}
