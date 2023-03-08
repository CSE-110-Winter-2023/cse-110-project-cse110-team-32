package com.example.team_32;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FakeUser {
    private String username;
    private String uid;

    public FakeUser(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

}

public class FakeDatabase {
    private static List<FakeUser> users = new ArrayList<>();
    private static Map<String, List<FakeUser>> relations = new HashMap<>();

    public static List<FakeUser> getUsers() {
        return users;
    }

    public static List<FakeUser> getFriends(String uid) {
        return relations.getOrDefault(uid, new ArrayList<>());
    }

    public static void setUsers(List<FakeUser> users) {
        FakeDatabase.users = users;
    }

    public static void addFriend(String uid, FakeUser friend) {

        if (relations.containsKey(uid)) {
            relations.get(uid).add(friend);

        } else {
            List<FakeUser> friends = FakeDatabase.getFriends(uid);
            friends.add(friend);
            relations.put(uid, friends);
        }
    }

}
