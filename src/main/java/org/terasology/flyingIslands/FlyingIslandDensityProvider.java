// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.joml.Vector3ic;
import org.joml.Vector3i;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.DensityFacet;
import org.terasology.world.generation.facets.SurfacesFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.Map;

@RegisterPlugin
@Requires(@Facet(value = FlyingIslandFacet.class, border = @FacetBorder(sides = FlyingIsland.MAX_WIDTH / 2, top = FlyingIsland.MAX_DEPTH)))
@Updates({
        @Facet(value = DensityFacet.class),
        @Facet(value = SurfacesFacet.class)
})
public class FlyingIslandDensityProvider implements FacetProviderPlugin {

    @Override
    public void process(GeneratingRegion region) {
        FlyingIslandFacet flyingIslandFacet = region.getRegionFacet(FlyingIslandFacet.class);
        DensityFacet densityFacet = region.getRegionFacet(DensityFacet.class);
        SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);

        for (Map.Entry<Vector3ic, FlyingIsland> entry : flyingIslandFacet.getWorldEntries().entrySet()) {

            Vector3i basePosition = new Vector3i(entry.getKey());
            FlyingIsland flyingIsland = entry.getValue();

            int extent = (int) flyingIsland.getOuterRadius();

            for (int i = -extent; i <= extent; i++) {
                for (int k = -extent; k <= extent; k++) {
                    Vector3i islandBasePosition = new Vector3i(i, FlyingIsland.MAX_DEPTH, k).add(basePosition);
                    Vector3i islandBasePosition2 = new Vector3i(islandBasePosition);

                    int depth = flyingIsland.getDepthRelativeToBase(islandBasePosition.x, islandBasePosition.z);
                    int height = flyingIsland.getHeightRelativeToBase(islandBasePosition.x, islandBasePosition.z);

                    Vector3i surface = islandBasePosition.add(0, height, 0);

                    if (depth > 0 && surfacesFacet.getWorldRegion().contains(islandBasePosition)) {
                        surfacesFacet.setWorld(surface, true);
                    } else {
                        int xx = 0;
                        xx++;
                        continue;
                    }

                    for (int j = height; j > -depth; j--) {
                        Vector3i position2 = new Vector3i(0, j, 0).add(islandBasePosition2);
                        if (densityFacet.getWorldRegion().contains(position2)) {
                            densityFacet.setWorld(position2, height - j);
                        }
                    }
                }
            }
        }
    }
}
