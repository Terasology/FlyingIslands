// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
@Requires({
        @Facet(value = ElevationFacet.class, border = @FacetBorder(sides = FlyingIsland.MAX_WIDTH / 2)),
        @Facet(value = SeaLevelFacet.class, border = @FacetBorder(sides = FlyingIsland.MAX_WIDTH / 2))
})
@Produces(FlyingIslandFacet.class)
public class FlyingIslandProvider implements FacetProviderPlugin {
    private Noise noise;

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(FlyingIslandFacet.class).extendBy(FlyingIsland.MAX_HEIGHT, FlyingIsland.MAX_DEPTH,
                FlyingIsland.MAX_WIDTH / 2);
        FlyingIslandFacet flyingIslandFacet = new FlyingIslandFacet(region.getRegion(), border);
        ElevationFacet elevationFacet = region.getRegionFacet(ElevationFacet.class);
        Rect2i worldRegion = elevationFacet.getWorldRegion();
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);


        for (int wz = worldRegion.minY(); wz <= worldRegion.maxY(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                int surfaceHeight = TeraMath.floorToInt(elevationFacet.getWorld(wx, wz));
                int seaLevel = seaLevelFacet.getSeaLevel();
                if (surfaceHeight > seaLevel && noise.noise(wx, wz) > 0.9999) {
                    int lowestY = surfaceHeight + 60 + Math.floorMod(wx * wz, 30);
                    FlyingIsland flyingIsland = new FlyingIsland(wx, lowestY, wz);
                    if (flyingIslandFacet.getWorldRegion().contains(wx, lowestY, wz)) {
                        flyingIslandFacet.setWorld(wx, lowestY, wz, flyingIsland);
                    }
                }
            }
        }

        region.setRegionFacet(FlyingIslandFacet.class, flyingIslandFacet);
    }

    @Override
    public void setSeed(long seed) {
        // comment this for testing and
        // noise = new SubSampledNoise(new WhiteNoise(seed), new Vector2f(0.1f, 0.1f), Integer.MAX_VALUE);
        // uncomment this for testing, Warning: this will spam flyingIslandes into the world
         noise = new WhiteNoise(seed);
    }
}
