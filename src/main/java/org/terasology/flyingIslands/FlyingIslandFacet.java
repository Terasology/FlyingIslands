// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseObjectFacet3D;

public class FlyingIslandFacet extends SparseObjectFacet3D<FlyingIsland> {

    public FlyingIslandFacet(BlockRegion targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
