package com.denireaux.fallingsand.fauna;

import java.util.ArrayList;
import java.util.List;

import com.denireaux.fallingsand.particletypes.Particle;

public class WormSegment extends Particle {

    private boolean isHead;
    private WormSegment next;
    private WormSegment prev;

    public WormSegment(int x, int y, boolean isHead) {
        super(x, y, "worm");
        this.isHead = isHead;
    }

    public void setNext(WormSegment next) {
        this.next = next;
    }

    public void setPrev(WormSegment prev) {
        this.prev = prev;
    }

    public WormSegment getNext() {
        return next;
    }

    public WormSegment getPrev() {
        return prev;
    }

    public boolean isHead() {
        return isHead;
    }

    @Override
    public void update(float gravity, Particle[][] grid) {
        // Only the head controls worm movement; body segments just get dragged.
        if (!isHead) return;

        // Optional: throttle movement so it's not jittering every single frame.
        // Tweak probability as desired (0.0 = always move, 0.9 = rarely).
        if (Math.random() < 0.3) {
            return;
        }

        // 1) Try to fall as a rigid body (entire worm down by 1)
        boolean fell = tryFallAsRigidBody(grid);

        // 2) If we can't fall, try to crawl sideways along the surface
        if (!fell) {
            tryCrawl(grid);
        }
    }

    /**
     * Try to move the entire worm one cell downward as a rigid body.
     * Returns true if all segments moved, false if blocked.
     */
    private boolean tryFallAsRigidBody(Particle[][] grid) {
        int width  = grid.length;
        int height = grid[0].length;

        List<WormSegment> segments = collectSegments();

        // Check all segments can move down one tile
        for (WormSegment seg : segments) {
            int newX = seg.x;
            int newY = seg.y - 1; // "down" (adjust if your sim uses the opposite)

            if (!inBounds(newX, newY, width, height)) {
                return false;
            }

            Particle below = grid[newX][newY];
            // Can move into empty or into our own worm segments
            if (below != null && !(below instanceof WormSegment)) {
                return false;
            }
        }

        // Move from tail to head so we don't overwrite ourselves
        for (int i = segments.size() - 1; i >= 0; i--) {
            WormSegment seg = segments.get(i);
            grid[seg.x][seg.y] = null;
            seg.y -= 1;
            grid[seg.x][seg.y] = seg;
        }

        return true;
    }

    /**
     * Try to crawl sideways along the ground.
     * Moves the head sideways and then pulls the rest of the body along its old positions.
     */
    private void tryCrawl(Particle[][] grid) {
        int width  = grid.length;
        int height = grid[0].length;

        // Only crawl if we're standing on something (i.e., not in mid-air)
        int belowX = x;
        int belowY = y - 1;
        if (!inBounds(belowX, belowY, width, height)) return;

        Particle below = grid[belowX][belowY];
        if (below == null) {
            // No support under head → we should be falling instead, so don't crawl
            return;
        }

        // Randomly pick a direction: -1 = left, +1 = right
        int dir = Math.random() < 0.5 ? -1 : 1;
        int targetX = x + dir;
        int targetY = y;

        if (!inBounds(targetX, targetY, width, height)) return;

        Particle target = grid[targetX][targetY];

        // Can't crawl into a non-worm solid
        if (target != null && !(target instanceof WormSegment)) {
            return;
        }

        // Save the previous position of the head
        int prevX = this.x;
        int prevY = this.y;

        // Move head to target
        moveSegmentTo(this, targetX, targetY, grid);

        // Now pull the rest of the segments along the old positions
        WormSegment current = this.next;
        while (current != null) {
            int oldX = current.x;
            int oldY = current.y;

            moveSegmentTo(current, prevX, prevY, grid);

            prevX = oldX;
            prevY = oldY;
            current = current.next;
        }
    }

    /**
     * Collect this head and all following segments into a list (head → tail).
     */
    private List<WormSegment> collectSegments() {
        List<WormSegment> list = new ArrayList<>();
        WormSegment current = this;
        while (current != null) {
            list.add(current);
            current = current.next;
        }
        return list;
    }

    /**
     * Move a single segment to (newX, newY) in the grid.
     */
    private void moveSegmentTo(WormSegment seg, int newX, int newY, Particle[][] grid) {
        grid[seg.x][seg.y] = null;
        seg.x = newX;
        seg.y = newY;
        grid[newX][newY] = seg;
    }

    private boolean inBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
