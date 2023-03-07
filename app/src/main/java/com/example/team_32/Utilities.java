package com.example.team_32;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utilities {
    public static Optional<Double> parseDouble(String str) {
        try {
            double number = Double.parseDouble(str);
            return Optional.of(number);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    public static List<FakeUser> getFakeUsers(){
        List<FakeUser> fakeUsers = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            FakeUser fakeUser = new FakeUser("User " + i, "" + i);
            fakeUsers.add(fakeUser);
        }
        return fakeUsers;
    }
    public static List<FakeUser> getFakeFriends(){
        List<FakeUser> fakeFriends = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            FakeUser fakeFriend = new FakeUser("Friend " + i, "" + i);
            fakeFriends.add(fakeFriend);
        }
        return fakeFriends;
    }
}
