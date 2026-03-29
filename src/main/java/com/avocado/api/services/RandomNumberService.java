package com.avocado.api.services;

import java.util.Random;

public class RandomNumberService {

    public long generate(String input) {
        long seed = computeSeed(input);
        return new Random(seed).nextLong();
    }

    private long computeSeed(String input) {
        long hash = 0;
        for (char c : input.toCharArray()) {
            hash = 31 * hash + c;
        }
        return hash;
    }
}
