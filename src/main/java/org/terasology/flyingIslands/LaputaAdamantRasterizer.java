// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.math.ChunkMath;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.RequiresRasterizer;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

import java.util.Map;
import java.util.Objects;

@RegisterPlugin
@RequiresRasterizer(SolidRasterizer.class)
public class LaputaAdamantRasterizer implements WorldRasterizerPlugin {
    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;
    private Block laputaAdamant;

    @Override
    public void initialize() {
        laputaAdamant = Objects.requireNonNull(CoreRegistry.get(BlockManager.class)).getBlock("FlyingIslands:LaputaAdamant");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        FlyingIslandFacet volcanoFacet = chunkRegion.getFacet(FlyingIslandFacet.class);

        for (Map.Entry<Vector3ic, FlyingIsland> entry : volcanoFacet.getWorldEntries().entrySet()) {

            FlyingIsland flyingIsland = entry.getValue();

            Vector3i bottom = flyingIsland.getBottommostPoint();

            if (chunk.getRegion().contains(bottom)) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(bottom, new Vector3i()), laputaAdamant);
            }
//            for (int i = -15; i <= 15; i++) {
//                Vector3i temp = new Vector3i(bottom);
//                if (chunk.getRegion().contains(temp.add(i, 0, i))) {
//                    chunk.setBlock(ChunkMath.calcRelativeBlockPos(bottom, new Vector3i()), LaputaAdamant);
//                }
//            }
        }
    }
}
