package com.denireaux.fallingsand.behaviors;

import com.denireaux.fallingsand.particletypes.Particle;

public interface IGas {
    void moveAsGas(Particle[][] grid, int x, int y);
}
