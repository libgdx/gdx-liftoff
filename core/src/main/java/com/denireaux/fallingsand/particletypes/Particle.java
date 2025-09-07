package com.denireaux.fallingsand.particletypes;

import java.util.HashMap;
import java.util.function.BiFunction;

import com.denireaux.fallingsand.helpers.MovementHelper;
import com.denireaux.fallingsand.utils.utils;

public abstract class Particle {
    public int x, y;
    public float velocity = 0f;
    public boolean isHot;
    public boolean willSink;
    protected final String id;

    public Particle(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public abstract void update(float gravity, Particle[][] grid);

    public Particle[] getSurroundingParticles(Particle[][] grid) {
        Particle left = (x > 0) ? grid[x - 1][y] : null;
        Particle right = (x < grid.length - 1) ? grid[x + 1][y] : null;
        Particle above = (y < grid[0].length - 1) ? grid[x][y + 1] : null;
        Particle below = (y > 0) ? grid[x][y - 1] : null;
    
        return new Particle[]{ left, right, above, below };
    }

    public String getId() {
        return this.id;
    };
    
    public void moveDown(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y - 1] = this;
        y--;
    }

    public void moveUp(Particle[][] grid) {
        grid[x][y] = null;
        grid[x][y + 1] = this;
        y++;
    }

    public void moveLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y] = this;
        x--;

    }

    public void moveRight(Particle[][] grid) {
        if (MovementHelper.canRight(grid, x, y)) {
            grid[x][y] = null;
            grid[x + 1][y] = this;
            x++;
        }
    }

    public void moveDownRight(Particle[][] grid) {
        grid[x][y] = null;
        grid[x + 1][y - 1] = this;
        x++;
        y--;
    }

    public void moveDownLeft(Particle[][] grid) {
        grid[x][y] = null;
        grid[x - 1][y - 1] = this;
        x--;
        y--;
    }

    public void tryNormalMovement(Particle[][] grid) {
        boolean canDown = MovementHelper.canMoveDown(grid, x, y);
        boolean canLeft = MovementHelper.canLeft(grid, x, y);
        boolean canRight = MovementHelper.canRight(grid, x, y);
        boolean canDownLeft = MovementHelper.canMoveDownLeft(grid, x, y);
        boolean canDownRight = MovementHelper.canMoveDownRight(grid, x, y);
        boolean leftFactor = utils.getRandomBoolean();

        if (canDown) {
            moveDown(grid);
            return;
        }

        if (canLeft && canRight) {
            if (leftFactor) {
                moveLeft(grid);
                return;
            }
            moveRight(grid);
        }

        if (canDownLeft && !canDownRight) {
            moveDownLeft(grid);
            return;
        }

        if (canDownRight && !canDownLeft) {
            moveDownRight(grid);
            return;
        }

        if (canDownLeft && canDownRight) {
            if (leftFactor) {
                moveDownLeft(grid);
                return;
            }
            moveDownRight(grid);
        }

    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    public void checkInbounds(Particle[][] grid, int x, int y) {
        if (y <= 0) return;
        if (x >= grid.length) return;
        if (y >= grid[0].length - 1) return;
    }

    public void swapWith(Particle[][] grid, int newX, int newY) {
        Particle temp = grid[newX][newY];
        grid[newX][newY] = this;
        grid[x][y] = temp;
        if (temp != null) {
            temp.x = x;
            temp.y = y;
        }
        x = newX;
        y = newY;
    }

    public void convertParticle(Particle[][] grid, int x, int y, String idOfDesiredParticle) {
        HashMap<String, BiFunction<Integer, Integer, Particle>> particleTypes = new HashMap<>();
        particleTypes.put("lava", (i, j) -> new LavaParticle(i, j, "lava"));
        particleTypes.put("water", (i, j) -> new WaterParticle(i, j, "water"));
        particleTypes.put("sand", (i, j) -> new SandParticle(i, j, "sand"));
        particleTypes.put("vapor", (i, j) -> new VaporParticle(i, j, "vapor"));
        particleTypes.put("void", (i, j) -> new VoidParticle(i, j, "void"));
        particleTypes.put("wetsand", (i, j) -> new WetSandParticle(i, j, "wetsand"));

        grid[x][y] = null;

        BiFunction<Integer, Integer, Particle> factory = particleTypes.get(idOfDesiredParticle);

        if (factory != null) {
            grid[x][y] = factory.apply(x, y);
        } else {
            System.out.println("Unknown particle type: " + idOfDesiredParticle);
        }
    }
    
}
