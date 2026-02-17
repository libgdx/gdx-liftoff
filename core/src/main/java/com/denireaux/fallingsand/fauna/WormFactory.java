package com.denireaux.fallingsand.fauna;

import com.denireaux.fallingsand.particletypes.Particle;

public class WormFactory {

    /**
     * Spawns a worm made of WormSegments at the given head position.
     * The worm is 6 pixels long (X direction) and 2 pixels tall (Y direction),
     * forming a solid 6×2 block of segments.
     * 
     * Head = (headX, headY)
     * Body = extends left (dx) and down (dy)
     */
    public static void spawnWorm(int headX, int headY, Particle[][] grid) {
        int width  = grid.length;
        int height = grid[0].length;

        // Worm shape config
        int wormLength = 6; // width of the worm in segments
        int wormHeight = 2; // height of the worm in segments

        // ---------------------------------------------------------
        // 1. BOUNDS + COLLISION CHECK
        // ---------------------------------------------------------
        // Ensure the 6×2 region is empty and inside the grid
        for (int dy = 0; dy < wormHeight; dy++) {
            for (int dx = 0; dx < wormLength; dx++) {
                int x = headX - dx;
                int y = headY + dy;

                if (x < 0 || x >= width || y < 0 || y >= height) return;
                if (grid[x][y] != null) return;
            }
        }

        WormSegment prev = null;

        // ---------------------------------------------------------
        // 2. CONSTRUCT HEAD → BODY → TAIL
        // ---------------------------------------------------------
        // Build in row-major order:
        // top-left of block ← head is at (dx=0, dy=0)
        //
        //   H M M M M M
        //   B B B B B B
        //
        for (int dy = 0; dy < wormHeight; dy++) {
            for (int dx = 0; dx < wormLength; dx++) {

                int x = headX - dx;  // extend to the LEFT
                int y = headY + dy;  // extend DOWNWARD for dy=1 (second row)

                boolean isHead = (dx == 0 && dy == 0);

                WormSegment seg = new WormSegment(x, y, isHead);
                grid[x][y] = seg;

                // Link previous → this
                if (prev != null) {
                    prev.setNext(seg);
                    seg.setPrev(prev);
                }

                prev = seg;
            }
        }

        // Done! Worm now exists in grid.
    }

}


// HEAD  seg2 seg3 seg4 seg5 seg6
// seg7 seg8 seg9 seg10 seg11 seg12

// head → seg2 → seg3 → … → seg12
