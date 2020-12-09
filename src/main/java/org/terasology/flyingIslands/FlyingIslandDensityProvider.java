/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
