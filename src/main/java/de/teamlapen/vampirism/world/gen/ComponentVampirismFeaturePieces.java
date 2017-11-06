package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.gen.structure.StructureManager;
import de.teamlapen.vampirism.world.gen.structure.VampirismTemplate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

/**
 * Load and place some of Vampirism's smaller structures
 */
public class ComponentVampirismFeaturePieces {
    static void registerPieces() {
        MapGenStructureIO.registerStructureComponent(House.class, "vampirism_house");
    }

    /**
     * Basic Structure Component to load Vampirism's structures
     */
    abstract static class ComponentTemplate extends StructureComponentTemplate {
        private StructureManager.Structure structure;
        private Rotation rotation;
        private Mirror mirror;
        private int heightOffset = -1;
        /**
         * If the ground below the structure should be filled up
         */
        private boolean fillUpGround = true;


        /**
         * DO NOT USE
         * Only for internal use
         */
        public ComponentTemplate() {

        }

        public ComponentTemplate(StructureManager.Structure structure, BlockPos position, Rotation rotation, Mirror mirror) {
            this.rotation = rotation;
            this.mirror = mirror;
            this.templatePosition = position;
            this.structure = structure;
            this.loadTemplate();
        }

        /**
         * Disable filling the ground below the structure
         */
        protected void disableFillUpGround() {
            fillUpGround = false;
        }

        public StructureManager.Structure getStructure() {
            return structure;
        }

        /**
         * Prevent automatic y offset depending on ground level
         */
        protected void disableGroundOffset() {
            this.heightOffset = 0;
        }

        private void loadTemplate() {
            VampirismTemplate template = StructureManager.get(structure);
            if (template == null) {
                throw new IllegalStateException("Can't find structure " + structure);
            }
            PlacementSettings placementSettings = new PlacementSettings().setIgnoreEntities(true).setRotation(rotation).setMirror(mirror);
            this.setup(template, this.templatePosition, placementSettings);
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setString("rotation", this.rotation.name());
            tagCompound.setString("mirror", this.mirror.name());
            tagCompound.setString("structure", this.structure.name());
            tagCompound.setInteger("yoffset", heightOffset);
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.structure = StructureManager.Structure.valueOf(tagCompound.getString("structure"));
            this.rotation = Rotation.valueOf(tagCompound.getString("rotation"));
            this.mirror = Mirror.valueOf(tagCompound.getString("mirror"));
            this.heightOffset = tagCompound.getInteger("yoffset");
            this.loadTemplate();
        }

        /**
         * Calculate average ground level and offset the template position accordingly
         */
        protected boolean offsetToAverageGroundLevel(World worldIn, StructureBoundingBox structurebb, int yOffset) {
            if (this.heightOffset >= 0) {
                return true;
            } else {
                int y = 0;
                int counted = 0;
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

                for (int z = this.boundingBox.minZ; z <= this.boundingBox.maxZ; z++) {
                    for (int x = this.boundingBox.minX; x <= this.boundingBox.maxX; x++) {
                        mutablePos.setPos(x, 64, z);

                        if (structurebb.isVecInside(mutablePos)) {
                            y += Math.max(worldIn.getTopSolidOrLiquidBlock(mutablePos).getY(), worldIn.provider.getAverageGroundLevel());
                            ++counted;
                        }
                    }
                }

                if (counted == 0) {
                    VampirismMod.log.w("ComponentTemplate", "Failed to calculate average ground level. BoundingBox of zero size");
                    return false;
                } else {
                    this.heightOffset = y / counted;
                    this.offset(0, this.heightOffset - this.boundingBox.minY + yOffset, 0);
                    return true;
                }
            }
        }

        /**
         * Fill the ground below solid ground level blocks of the structure with filler blocks from the biome
         * @param world The world
         * @param structureBox The box of the structure
         * @param bb The box limiting the generation
         */
        protected void fillUpGround(World world, StructureBoundingBox structureBox, StructureBoundingBox bb) {
            Biome b = world.getBiome(this.templatePosition);

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            int y = this.templatePosition.getY();
            for (int x = structureBox.minX; x <= structureBox.maxX; x++) {
                for (int z = structureBox.minZ; z <= structureBox.maxZ; z++) {

                    pos.setPos(x, y, z);
                    if (bb.isVecInside(pos)) {
                        if (!world.isAirBlock(pos)) {
                            pos.setY(pos.getY() - 1);
                            while ((world.isAirBlock(pos) || world.getBlockState(pos).getMaterial().isLiquid()) && pos.getY() > 1) {
                                world.setBlockState(pos, b.fillerBlock, 2);
                                pos.setY(pos.getY() - 1);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, -1)) {
                return false;
            } else {
                boolean flag = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
                if (flag && fillUpGround) {
                    fillUpGround(worldIn, this.getBoundingBox(), structureBoundingBoxIn);
                }
                return flag;
            }
        }
    }

    /**
     * Small house
     */
    public static class House extends ComponentTemplate {


        /**
         * DON'T USE
         * INTERNAL
         */
        public House() {
        }

        public House(BlockPos position, Rotation rotation, Mirror mirror) {
            super(StructureManager.Structure.HOUSE1, position, rotation, mirror);
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {

        }

    }


}
