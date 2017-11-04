package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Map;
import java.util.Random;

public class ComponentVampirismFeaturePieces {
     static void registerPieces(){
        MapGenStructureIO.registerStructureComponent(Igloo.class,"vampirism_igloo");
    }

    abstract static class Feature extends StructureComponent {
        /**
         * The size of the bounding box for this feature in the X axis
         */
        protected int width;
        /**
         * The size of the bounding box for this feature in the Y axis
         */
        protected int height;
        /**
         * The size of the bounding box for this feature in the Z axis
         */
        protected int depth;
        protected int horizontalPos = -1;

        /**
         * DONT USE
         * USED INTERNALY
         */
        @Deprecated
        public Feature()
        {
        }

        protected Feature(Random rand, int x, int y, int z, int sizeX, int sizeY, int sizeZ)
        {
            super(0);
            this.width = sizeX;
            this.height = sizeY;
            this.depth = sizeZ;
            this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(rand));

            if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z)
            {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1);
            }
            else
            {
                this.boundingBox = new StructureBoundingBox(x, y, z, x + sizeZ - 1, y + sizeY - 1, z + sizeX - 1);
            }
        }

        /**
         * (abstract) Helper method to write subclass data to NBT
         */
        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setInteger("Width", this.width);
            tagCompound.setInteger("Height", this.height);
            tagCompound.setInteger("Depth", this.depth);
            tagCompound.setInteger("HPos", this.horizontalPos);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
        {
            this.width = tagCompound.getInteger("Width");
            this.height = tagCompound.getInteger("Height");
            this.depth = tagCompound.getInteger("Depth");
            this.horizontalPos = tagCompound.getInteger("HPos");
        }

        /**
         * Calculates and offsets this structure boundingbox to average ground level
         */
        protected boolean offsetToAverageGroundLevel(World worldIn, StructureBoundingBox structurebb, int yOffset)
        {
            if (this.horizontalPos >= 0)
            {
                return true;
            }
            else
            {
                int i = 0;
                int j = 0;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k)
                {
                    for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l)
                    {
                        blockpos$mutableblockpos.setPos(l, 64, k);

                        if (structurebb.isVecInside(blockpos$mutableblockpos))
                        {
                            i += Math.max(worldIn.getTopSolidOrLiquidBlock(blockpos$mutableblockpos).getY(), worldIn.provider.getAverageGroundLevel());
                            ++j;
                        }
                    }
                }

                if (j == 0)
                {
                    return false;
                }
                else
                {
                    this.horizontalPos = i / j;
                    this.boundingBox.offset(0, this.horizontalPos - this.boundingBox.minY + yOffset, 0);
                    return true;
                }
            }
        }
    }

    public static class Igloo extends ComponentVampirismFeaturePieces.Feature
    {
        private static final ResourceLocation IGLOO_TOP_ID = new ResourceLocation("igloo/igloo_top");
        private static final ResourceLocation IGLOO_MIDDLE_ID = new ResourceLocation("igloo/igloo_middle");
        private static final ResourceLocation IGLOO_BOTTOM_ID = new ResourceLocation("igloo/igloo_bottom");


        /**
         * DONT USE
         */
        @Deprecated
        public Igloo()
        {
        }

        public Igloo(Random rand, int x, int z)
        {
            super(rand, x, 64, z, 70, 5, 80);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
         * Mineshafts at the end, it adds Fences...
         */
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            VampirismMod.log.t("Placing Iglooo %s",structureBoundingBoxIn);
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, -1))
            {
                return false;
            }
            else
            {
                StructureBoundingBox structureboundingbox = this.getBoundingBox();
                BlockPos blockpos = new BlockPos(structureboundingbox.minX, structureboundingbox.minY, structureboundingbox.minZ);
                Rotation[] arotation = Rotation.values();
                MinecraftServer minecraftserver = worldIn.getMinecraftServer();
                TemplateManager templatemanager = worldIn.getSaveHandler().getStructureTemplateManager();
                PlacementSettings placementsettings = (new PlacementSettings()).setRotation(arotation[randomIn.nextInt(arotation.length)]).setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(structureboundingbox);
                Template template = templatemanager.getTemplate(minecraftserver, IGLOO_TOP_ID);
                template.addBlocksToWorldChunk(worldIn, blockpos, placementsettings);

                if (randomIn.nextDouble() < 0.5D)
                {
                    Template template1 = templatemanager.getTemplate(minecraftserver, IGLOO_MIDDLE_ID);
                    Template template2 = templatemanager.getTemplate(minecraftserver, IGLOO_BOTTOM_ID);
                    int i = randomIn.nextInt(8) + 4;

                    for (int j = 0; j < i; ++j)
                    {
                        BlockPos blockpos1 = template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - j * 3, 5), placementsettings, new BlockPos(1, 2, 1));
                        template1.addBlocksToWorldChunk(worldIn, blockpos.add(blockpos1), placementsettings);
                    }

                    BlockPos blockpos4 = blockpos.add(template.calculateConnectedPos(placementsettings, new BlockPos(3, -1 - i * 3, 5), placementsettings, new BlockPos(3, 5, 7)));
                    template2.addBlocksToWorldChunk(worldIn, blockpos4, placementsettings);
                    Map<BlockPos, String> map = template2.getDataBlocks(blockpos4, placementsettings);

                    for (Map.Entry<BlockPos, String> entry : map.entrySet())
                    {
                        if ("chest".equals(entry.getValue()))
                        {
                            BlockPos blockpos2 = entry.getKey();
                            worldIn.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 3);
                            TileEntity tileentity = worldIn.getTileEntity(blockpos2.down());

                            if (tileentity instanceof TileEntityChest)
                            {
                                ((TileEntityChest)tileentity).setLootTable(LootTableList.CHESTS_IGLOO_CHEST, randomIn.nextLong());
                            }
                        }
                    }
                }
                else
                {
                    BlockPos blockpos3 = Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5));
                    worldIn.setBlockState(blockpos.add(blockpos3), Blocks.SNOW.getDefaultState(), 3);
                }

                return true;
            }
        }
    }

}
