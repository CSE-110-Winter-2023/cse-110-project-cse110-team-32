package com.example.team_32;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void testParseDouble(){
        assert Utilities.parseDouble("1.0").isPresent();
        assert Utilities.parseDouble("1").isPresent();
        assert Utilities.parseDouble("1.0").get() == 1.0;
        assert Utilities.parseDouble("1").get() == 1.0;
        assert !Utilities.parseDouble("a").isPresent();
    }

}