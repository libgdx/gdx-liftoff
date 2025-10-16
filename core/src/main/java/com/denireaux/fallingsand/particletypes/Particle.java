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
    public int sinkCounter = 0;
    protected static final int SINK_DELAY = 120;
    protected boolean isStatic;
    protected boolean isGas;
    protected boolean isCombustable;
    protected boolean hasCombusted;
    protected String id;
    protected int size = 1;
    public boolean isWet;


    public Particle(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public abstract void update(float gravity, Particle[][] grid);

    public void update(float gravity, Particle[][] current, Particle[][] next) {
        // default: just call old update on current grid,
        // then copy into next
        update(gravity, current);
        next[x][y] = this;
    }

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
        return;

    }

    public void moveRight(Particle[][] grid) {
        if (MovementHelper.canRight(grid, x, y)) {
            grid[x][y] = null;
            grid[x + 1][y] = this;
            x++;
        }
        return;
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

    public void moveTo(Particle[][] grid, int destinationsX, int destinatinsY) {
        grid[x][y] = null;
        x = destinationsX;
        y = destinatinsY;
        grid[x][y] = this;
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

    public Particle getBelowParticle(Particle[][] grid, int x, int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        return surroundings[3];
    }

    public Particle getAboveParticle(Particle[][] grid, int x , int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        return surroundings[2];
    }

    public Particle getRightParticle(Particle[][] grid, int x, int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        return surroundings[1];
    }

    public Particle getLeftParticle(Particle[][] grid, int x, int y) {
        Particle[] surroundings = getSurroundingParticles(grid);
        return surroundings[0];
    }

    public void swapWith(Particle[][] grid, int newX, int newY) {
        if (newX >= grid.length || newX <= 0) return;
        if (newY >= grid[0].length - 1) return;
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
        particleTypes.put("smoke", (i, j) -> new SmokeParticle(i, j, "smoke"));
        particleTypes.put("powder", (i, j) -> new PowderParticle(i, j, "powder"));
        particleTypes.put("carbon", (i, j) -> new CarbonParticle(i, j, "carbon"));
        particleTypes.put("snow", (i, j) -> new SnowParticle(i, j, "snow"));
        particleTypes.put("oil", (i, j) -> new OilParticle(i, j, "oil"));
        grid[x][y] = null;
        BiFunction<Integer, Integer, Particle> factory = particleTypes.get(idOfDesiredParticle);
        if (factory != null) grid[x][y] = factory.apply(x, y);
    }

    public void trySinking(Particle[][] grid, int x, int y) {
        Particle particleBelow = getSurroundingParticles(grid)[3];
        if (particleBelow == null) return;
        if ("water".equals(particleBelow.getId())) {
            if (sinkCounter >= SINK_DELAY) {
                swapWith(grid, x, y - 1);
                sinkCounter = 0;
            } else {
                sinkCounter++;
            }
        } else {
            sinkCounter = 0;
        }
    }

    public void trySwappingWithRight(Particle[][] grid, int x, int y) {
        Particle particleRight = getRightParticle(grid, x, y);
        if (particleRight == null || particleRight.isStatic) return;
        swapWith(grid, particleRight.x, y);
    }

    public void trySwappingWithLeft(Particle[][] grid, int x, int y) {
        Particle particleLeft = getLeftParticle(grid, x, y);
        if (particleLeft == null || particleLeft.isStatic) return;
        swapWith(grid, particleLeft.x, y);
    }

    public void deleteSelf(Particle[][] grid, int x, int y) {
        grid[x][y] = null;
    }

}
