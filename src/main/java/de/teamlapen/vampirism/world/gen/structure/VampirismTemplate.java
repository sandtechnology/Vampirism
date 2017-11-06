package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

public class VampirismTemplate extends Template {

    @Override
    public void addBlocksToWorld(World worldIn, BlockPos pos, PlacementSettings placementIn) {
        this.addBlocksToWorld(worldIn, pos, placementIn, 2);
    }

    @Override
    public void addBlocksToWorld(World worldIn, BlockPos pos, PlacementSettings placementIn, int flags) {
        Biome b = worldIn.getBiome(pos);
        if (b == null) {
            VampirismMod.log.e("VampirismTemplate", "Biomes is somehow null. Should be detected at an earlier stage");
            return;
        }
        this.addBlocksToWorld(worldIn, pos, new TemplateProcessor(b.fillerBlock, b.topBlock, placementIn, pos), placementIn, flags);
    }

    public static class TemplateProcessor implements ITemplateProcessor {
        private final Block toSurface;
        private final Block toFiller;
        private final Block toSkip;
        private final IBlockState filler;
        private final IBlockState surface;

        private final float chance;
        private final Random random;


        /**
         * Process the template blocks by replacing the placeholder blocks as well as skipping some blocks if the structure is damaged (integrity)
         *
         * @param filler   To replace the filler placeholder block
         * @param surface  To replace the surface placeholder block
         * @param settings
         * @param pos      Only used as a seed for the random
         */
        public TemplateProcessor(IBlockState filler, IBlockState surface, PlacementSettings settings, BlockPos pos) {
            this.filler = filler;
            this.surface = surface;
            toSkip = Blocks.SPONGE;
            toFiller = Blocks.BEDROCK;
            toSurface = Blocks.MYCELIUM;
            this.chance = settings.getIntegrity();
            this.random = settings.getRandom(pos);
        }

        @Nullable
        @Override
        public BlockInfo processBlock(World worldIn, BlockPos pos, BlockInfo blockInfoIn) {
            if (this.chance < 1.0F && this.random.nextFloat() > this.chance) {
                return null;
            }
            Block b = blockInfoIn.blockState.getBlock();
            if (toSkip.equals(b)) {
                return null;
            } else if (toFiller.equals(b)) {
                return new BlockInfo(blockInfoIn.pos, filler, null);
            } else if (toSurface.equals(b)) {
                return new BlockInfo(blockInfoIn.pos, surface, null);
            }
            return blockInfoIn;
        }
    }
}
