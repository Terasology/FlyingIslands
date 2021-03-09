// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.flyingIslands;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.RequiresRasterizer;
import org.terasology.engine.world.generation.WorldRasterizerPlugin;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;

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
        FlyingIslandFacet flyingIslandFacet = chunkRegion.getFacet(FlyingIslandFacet.class);

        for (Map.Entry<Vector3ic, FlyingIsland> entry : flyingIslandFacet.getWorldEntries().entrySet()) {

            FlyingIsland flyingIsland = entry.getValue();

            Vector3i bottom = flyingIsland.getBottommostPoint();

            if (chunk.getRegion().contains(bottom)) {
                chunk.setBlock(Chunks.toRelative(bottom), laputaAdamant);
            }
        }
    }
}
