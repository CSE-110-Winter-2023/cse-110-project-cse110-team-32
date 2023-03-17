package com.example.team_32;

import static org.junit.Assert.*;
import android.util.Pair;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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

    private static double DELTA = 1e-4; // Tolerance for floating-point comparisons

    @Test
    public void testToCartesian() {
        double latitude = 32.715736; // Example latitude value
        double longitude = -117.161087; // Example longitude value

        double[] expected = { -13027.718474561763, 3853.316561579488 };
        double[] result = Utilities.toCartesian(latitude, longitude);

        assertArrayEquals(expected, result, DELTA);
    }

    @Test
    public void testGetRelativeVector() {
        Pair<Double, Double> loc1 = new android.util.Pair<>(32.715736, -117.161087);
        Pair<Double, Double> loc2 = new android.util.Pair<>(32.721736, -117.151087);
        System.out.println("Pos2"+ "Getting vector  between"+  loc1.first.toString() + " and " + loc2.first.toString());
        float[] expected = { 1.1119493f, 0.7929901f };
        float[] result = Utilities.getRelativeVector(new android.util.Pair<Double, Double>(32.715736, -117.161087), new android.util.Pair<Double,Double>(32.721736, -117.151087));

        assertArrayEquals(expected, result, (float) DELTA);
    }

    @Test
    public void testDistanceInMiles() {
        float[] vector = { 10279.078f, 664.8709f };
        float expected = 6400.4682617f;

        float result = Utilities.distanceInMiles(vector);
        assertEquals(expected, result, DELTA);
    }
}