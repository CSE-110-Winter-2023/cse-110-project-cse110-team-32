package com.example.team_32;

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
}
