// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.terasology.math.JomlUtil;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
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
@Requires(@Facet(value = FlyingIslandFacet.class, border = @FacetBorder(sides = FlyingIsland.MAXWIDTH / 2, top = FlyingIsland.MAXHEIGHT)))
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

        for (Map.Entry<BaseVector3i, FlyingIsland> entry : flyingIslandFacet.getWorldEntries().entrySet()) {

            Vector3i basePosition = new Vector3i(entry.getKey());
            FlyingIsland flyingIsland = entry.getValue();

            int extent = (int) flyingIsland.getOuterRadius();
            int top = FlyingIsland.MAXHEIGHT;

            for (int i = -extent; i <= extent; i++) {
                for (int k = -extent; k <= extent; k++) {
                    Vector3i position = new Vector3i(i, top, k).add(basePosition);

                    int height = flyingIsland.getHeightAndIsLava(position.x, position.z);

                    if (height > 0 && surfacesFacet.getWorldRegion().encompasses(position)) {
                        surfacesFacet.setWorld(JomlUtil.from(position), true);
                    }

                    for (int j = top; j > top - height; j--) {
                        Vector3i position2 = new Vector3i(i, j, k).add(basePosition);
                        if (densityFacet.getWorldRegion().encompasses(position2)) {
                            densityFacet.setWorld(position2, top - j + 1);
                        }
                    }
                }
            }
        }
    }
}
