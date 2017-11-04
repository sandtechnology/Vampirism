package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

import javax.annotation.Nullable;
import java.util.Random;

public class MapGenVampirismFeatures extends MapGenStructure
{

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

    /** the maximum distance between scattered features */
    private final int maxDistanceBetweenScatteredFeatures;
    /** the minimum distance between scattered features */
    private final int minDistanceBetweenScatteredFeatures;

    public MapGenVampirismFeatures() {
        minDistanceBetweenScatteredFeatures = 2;
        maxDistanceBetweenScatteredFeatures = 8;
    }


    @Override public String getStructureName()
    {
        return id;
    }

    @Nullable @Override public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures, 14357617, false, 100, findUnexplored);
    }

    @Override protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);

        if (i == k && j == l)
        {
           return true;
        }

        return false;
    }

    @Override protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new Start(this.world,this.rand,chunkX,chunkZ);
    }

    public static class Start extends StructureStart
    {
        /**
         * DONT USE
         * INTERNAL
         */
        @Deprecated
        public Start()
        {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ)
        {
            this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn)
        {
            super(chunkX, chunkZ);
            ComponentVampirismFeaturePieces.Igloo igloo = new ComponentVampirismFeaturePieces.Igloo(random, chunkX * 16, chunkZ * 16);
            this.components.add(igloo);

            VampirismMod.log.t("Start at %s %s",chunkX,chunkZ);

            this.updateBoundingBox();
        }
    }
}
