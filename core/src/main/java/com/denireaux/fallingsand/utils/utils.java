package com.denireaux.fallingsand.utils;

import java.util.Random;

public class utils {
    public static boolean getRandomBoolean() {
        Random rand = new Random();
        boolean randomBool = rand.nextBoolean();

        return randomBool;
    }
}
