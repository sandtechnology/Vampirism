package de.teamlapen.vampirism.world.gen;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.init.Biomes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Map gen for Vampirism's smaller structures
 */
public class MapGenVampirismFeatures extends MapGenStructure
{

    private static final List<Biome> BIOMELIST = Arrays.asList(ModBiomes.vampireForest, Biomes.ROOFED_FOREST, Biomes.DESERT, Biomes.PLAINS, Biomes.FOREST, Biomes.MESA, Biomes.SAVANNA, Biomes.ICE_PLAINS, Biomes.COLD_TAIGA, Biomes.TAIGA);

    public static final String id="vampirism-structures";


    public static void doRegister(){
        MapGenStructureIO.registerStructure(Start.class,"vampirism_structures");
        ComponentVampirismFeaturePieces.registerPieces();
    }

    private static MapGenVampirismFeatures instance;

    public static MapGenVampirismFeatures getInstance(){
        if(instance==null){
            instance=new MapGenVampirismFeatures();
        }
        return instance;
    }

    private final int maxDistance;
    private final int minDistance;

    public MapGenVampirismFeatures() {
        minDistance = 2;
        maxDistance = 8;//TODO use reasonable values
    }


    @Override public String getStructureName() {
        return id;
    }

    @Nullable @Override public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.maxDistance, this.minDistance, 14357617, false, 100, findUnexplored);
    }

    @Override protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0) {
            chunkX -= this.maxDistance - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= this.maxDistance - 1;
        }

        int k = chunkX / this.maxDistance;
        int l = chunkZ / this.maxDistance;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistance;
        l = l * this.maxDistance;
        k = k + random.nextInt(this.maxDistance - minDistance);
        l = l + random.nextInt(this.maxDistance - minDistance);

        if (i == k && j == l) {
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biome == null) {
                return false;
            }

            for (Biome biome1 : BIOMELIST) {
                if (biome == biome1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.world,this.rand,chunkX,chunkZ);
    }

    public static class Start extends StructureStart {
        /**
         * DONT USE
         * INTERNAL
         */
        @Deprecated
        public Start() {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ) {
            this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn) {
            super(chunkX, chunkZ);
            Mirror mirror = UtilLib.chooseRandom(Mirror.values(), random);
            Rotation rotation = UtilLib.chooseRandom(Rotation.values(), random);
            BlockPos pos = new BlockPos(chunkX * 16, 64, chunkZ * 16);
            ComponentVampirismFeaturePieces.House igloo = new ComponentVampirismFeaturePieces.House(pos, rotation, mirror);
            this.components.add(igloo);
            this.updateBoundingBox();
        }
    }
}
