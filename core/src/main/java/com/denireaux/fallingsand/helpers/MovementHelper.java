package com.denireaux.fallingsand.helpers;

import com.denireaux.fallingsand.particletypes.Particle;

public class MovementHelper {

    public static boolean canMoveDown(Particle[][] grid, int x, int y) {
        return y > 0 && grid[x][y - 1] == null;
    }

    public static boolean canLeft(Particle [][] grid, int x, int y) {
        return x > 0 && grid[x - 1][y] == null;
    }

    public static boolean canRight(Particle [][] grid, int x, int y) {
        return x + 1 < grid.length && grid[x + 1][y] == null;
    }

    public static boolean canMoveDownLeft(Particle[][] grid, int x, int y) {
        return x > 0 && y > 0 && grid[x - 1][y - 1] == null && grid[x - 1][y] == null;
    }

    public static boolean canMoveDownRight(Particle[][] grid, int x, int y) {
        return x < grid.length - 1 && y > 0 && grid[x + 1][y - 1] == null && grid[x + 1][y] == null;
    }

    public static boolean canMoveUp(Particle[][] grid, int x, int y) {
        return y < grid[0].length - 1 && grid[x][y + 1] == null;
    }
}
