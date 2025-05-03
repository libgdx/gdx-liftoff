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

        /**
     * Returns a shuffled version of the given int array.
     *
     * @param array the input array to shuffle
     * @return a new int array with shuffled order
     */
    public static int[] getShuffledArray(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int i : array) list.add(i);
        Collections.shuffle(list);
        return list.stream().mapToInt(i -> i).toArray();
    }
}
