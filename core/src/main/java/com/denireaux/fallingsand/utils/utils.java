package com.denireaux.fallingsand.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class utils {
    public static boolean getRandomBoolean() {
        Random rand = new Random();
        boolean randomBool = rand.nextBoolean();
        return randomBool;
    }

    public static int[] getShuffledArray(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int i : array) list.add(i);
        Collections.shuffle(list);
        return list.stream().mapToInt(i -> i).toArray();
    }

    public static boolean getUnfairBoolean(int targetNumber) {
        Random rand = new Random();
        int randomNumberBetweenOneAndFifty = rand.nextInt(50);
        if (randomNumberBetweenOneAndFifty == targetNumber) return true;
        return false;
    }
}
