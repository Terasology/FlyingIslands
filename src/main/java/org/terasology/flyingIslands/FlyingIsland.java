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
    public static final int MINHEIGHT = 10;
    public static final int MAXHEIGHT = 20;
    public static final int MINGRIDSIZE = 6;
    public static final int MAXGRIDSIZE = 9;
    public static final int MINRADIUS = 40;
    public static final int MAXRADIUS = 60;
    public static final int MAXWIDTH = 2 * MAXRADIUS;
    private static final float NOISESUBSAMPLINGCONSTANT = MINHEIGHT / 4f;

    public int height;

    // Mind that these values will be used for comparisons *after* squaring the base noise value
    private final float outerRadius;
    private final float innerRadius;
    private final Vector2i center;

    private final Noise tileableNoise;
    private final RegionSelectorNoise regionNoise;

    public FlyingIsland(int xCenter, int zCenter) {
        int seed = xCenter + zCenter;
        FastRandom random = new FastRandom(seed);
        int gridSize = random.nextInt(MINGRIDSIZE, MAXGRIDSIZE);
        tileableNoise = new SimplexNoise(seed, gridSize);
        height = random.nextInt(MINHEIGHT, MAXHEIGHT);
        innerRadius = random.nextFloat(MINRADIUS, (MAXRADIUS + 2f * MINRADIUS) / 3);

        center = new Vector2i(xCenter, zCenter);

        outerRadius = random.nextFloat((MINRADIUS + 2f * MAXRADIUS) / 3, MAXRADIUS);
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

    public int getHeightAndIsLava(int x, int z) {
        float baseNoise = regionNoise.noise(x, z);

        // another noise layer to make the FlyingIsland slope curvy
        float plainNoise = tileableNoise.noise(x / NOISESUBSAMPLINGCONSTANT, z / NOISESUBSAMPLINGCONSTANT);
        float noiseSquare = (float) Math.pow(baseNoise, 3f);
        float mixedNoise = (noiseSquare * (1 + plainNoise / 10f)) / 1.1f;

        return (int) (mixedNoise * height);
    }
}
