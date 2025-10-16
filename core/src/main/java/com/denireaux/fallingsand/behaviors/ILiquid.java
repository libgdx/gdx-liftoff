package com.denireaux.fallingsand.behaviors;

import com.denireaux.fallingsand.particletypes.Particle;

public interface ILiquid {
    void moveAsLiquid(Particle[][] grid, int x, int y);
}
