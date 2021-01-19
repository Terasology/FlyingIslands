// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

//import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector2i;
//import org.terasology.math.geom.Vector3i;
//import org.terasology.nui.properties.Range;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.RegionSelectorNoise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.random.FastRandom;

public class FlyingIsland {
    public static final int MIN_DEPTH = 10;
    public static final int MAX_DEPTH = 20;
    public static final int MIN_GRID_SIZE = 6;
    public static final int MAX_GRID_SIZE = 9;
    public static final int MIN_RADIUS = 40;
    public static final int MAX_RADIUS = 60;
    public static final int MAX_WIDTH = 2 * MAX_RADIUS;
    public static final int MAX_HEIGHT = 3;
    private static final float NOISE_SUBSAMPLING_CONSTANT = MIN_DEPTH / 4f;

    public int depth;

    // Mind that these values will be used for comparisons *after* squaring the base noise value
    private final float outerRadius;
    private final float innerRadius;
    private final Vector2i center;

    private final Noise tileableNoise;
    private final RegionSelectorNoise regionNoise;

    public FlyingIsland(int xCenter, int zCenter) {
        int seed = xCenter + zCenter;
        FastRandom random = new FastRandom(seed);
        int gridSize = random.nextInt(MIN_GRID_SIZE, MAX_GRID_SIZE);
        tileableNoise = new SimplexNoise(seed, gridSize);
        depth = random.nextInt(MIN_DEPTH, MAX_DEPTH);
        innerRadius = random.nextFloat(MIN_RADIUS, (MAX_RADIUS + 2f * MIN_RADIUS) / 3);

        center = new Vector2i(xCenter, zCenter);

        outerRadius = random.nextFloat((MIN_RADIUS + 2f * MAX_RADIUS) / 3, MAX_RADIUS);
        regionNoise = new RegionSelectorNoise(seed, gridSize, center.x(), center.y(), innerRadius, outerRadius);
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public Vector2i getCenter() {
        return center;
    }

    private float getDepthNoise(int x, int z) {
        float baseNoise = regionNoise.noise(x, z);

        // another noise layer to make the FlyingIsland slope curvy
        float plainNoise = tileableNoise.noise(x / NOISE_SUBSAMPLING_CONSTANT, z / NOISE_SUBSAMPLING_CONSTANT);
        float noiseSquare = (float) Math.pow(baseNoise, 3f);
        float mixedNoise = (noiseSquare * (1 + plainNoise / 10f)) / 1.1f;
        return mixedNoise;
    }

    public int getDepthRelativeToBase(int x, int z) {
        return (int) (getDepthNoise(x, z) * depth);
    }

    public int getHeightRelativeToBase(int x, int z) {
        float depthNoise = getDepthNoise(x, z);
        float multiplier = 1f;
        if (depthNoise < 0.1f) {
            multiplier = depthNoise * 10f;
        }

        return MAX_HEIGHT - (int) (tileableNoise.noise((x + 1337) / 20f, (z - 1337) / 20f) * multiplier * MAX_HEIGHT);
    }
}
