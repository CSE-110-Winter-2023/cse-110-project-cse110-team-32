package com.example.team_32;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void testParseDoublePosInteger(){
        assert Utilities.parseDouble("1").isPresent();
        assert Utilities.parseDouble("1").get() == 1.0;
    }
    @Test
    public void testParseDoublePosDouble(){
        assert Utilities.parseDouble("1.8").isPresent();
        assert Utilities.parseDouble("1.8").get() == 1.8;
    }

    @Test
    public void testParseDoubleNegInteger(){
        assert Utilities.parseDouble("-3").isPresent();
        assert Utilities.parseDouble("-4").get() == -4.0;
    }
    @Test
    public void testParseDoubleNegDouble(){
        assert Utilities.parseDouble("-23.4").isPresent();
        assert Utilities.parseDouble("-43.2").get() == -43.2;
    }

    @Test
    public void testParseDoubleZero(){
        assert Utilities.parseDouble("0").isPresent();
        assert Utilities.parseDouble("0.0").get() == 0.0;
    }

    @Test
    public void testParseDoubleInvalidText() {
        assert !Utilities.parseDouble("Invalid Number").isPresent();
    }


}