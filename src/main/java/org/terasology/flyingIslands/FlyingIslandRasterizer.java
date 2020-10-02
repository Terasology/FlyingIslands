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

import org.terasology.math.ChunkMath;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

import java.util.Map;
import java.util.Objects;

@RegisterPlugin
public class FlyingIslandRasterizer implements WorldRasterizerPlugin {
    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;
    private Block dirt;

    @Override
    public void initialize() {
        dirt = Objects.requireNonNull(CoreRegistry.get(BlockManager.class)).getBlock("CoreAssets:Dirt");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        FlyingIslandFacet flyingIslandFacet = chunkRegion.getFacet(FlyingIslandFacet.class);

        for (Map.Entry<BaseVector3i, FlyingIsland> entry : flyingIslandFacet.getWorldEntries().entrySet()) {

            Vector3i basePosition = new Vector3i(entry.getKey());
            FlyingIsland flyingIsland = entry.getValue();

            int extent = (int) flyingIsland.getOuterRadius();
            int top = FlyingIsland.MAXHEIGHT;

            for (int i = -extent; i <= extent; i++) {
                for (int k = -extent; k <= extent; k++) {
                    Vector3i chunkBlockPosition = new Vector3i(i, 0, k).add(basePosition);

                    int height = flyingIsland.getHeightAndIsLava(chunkBlockPosition.x, chunkBlockPosition.z);

                    for (int j = top; j > top - height; j--) {
                        Vector3i chunkBlockPosition2 = new Vector3i(i, j, k).add(basePosition);
                        if (chunk.getRegion().encompasses(chunkBlockPosition2)) {
                            chunk.setBlock(ChunkMath.calcRelativeBlockPos(chunkBlockPosition2), dirt);
//                            switch (blockInfo.block) {
//                                case LAVA: chunk.setBlock(ChunkMath.calcRelativeBlockPos(chunkBlockPosition2), lava);
//                                break;
//                                case SLATE: chunk.setBlock(ChunkMath.calcRelativeBlockPos(chunkBlockPosition2), slate);
//                                break;
//                                case BASALT: chunk.setBlock(ChunkMath.calcRelativeBlockPos(chunkBlockPosition2), basalt);
//                            }
                        }
                    }
                }
            }

        }
    }
}
