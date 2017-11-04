package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModGenEventListener {

    @SubscribeEvent
    public void onChunkGenerate(ChunkGeneratorEvent.Generate event){
        MapGenVampirismFeatures.getInstance().generate(event.getWorld(),event.getX(),event.getZ(),event.getPrimer());
    }

    @SubscribeEvent
    public void onChunkGetNearestStructure(ChunkGeneratorEvent.FindStructure event){
        if(MapGenVampirismFeatures.id.equals(event.getStructureName())){
            event.setCanceled(true);
            event.setFoundPos(MapGenVampirismFeatures.getInstance().getNearestStructurePos(event.getWorldIn(),event.getPos(),event.isFindUnexplored()));
        }
    }

    @SubscribeEvent
    public void onChunkStructureCheck(ChunkGeneratorEvent.StructureCheck event){
        if(MapGenVampirismFeatures.id.equals(event.getStructureName())){
            event.setCanceled(true);
            event.setInStructure(MapGenVampirismFeatures.getInstance().isInsideStructure(event.getPos()));
        }
    }

    @SubscribeEvent
    public void onChunkStructureCheck(ChunkGeneratorEvent.GetPossibleCreatures event){
        if(event.getCreatureType() == EnumCreatureType.MONSTER && MapGenVampirismFeatures.getInstance().isPositionInStructure(event.getWorld(),event.getPos())){
            event.setCanceled(true);
            event.getPossibleCreatures().add(new Biome.SpawnListEntry(EntityGhost.class, 100, 1, 3));
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChunkPopulate(PopulateChunkEvent.Pre event){
        MapGenVampirismFeatures.getInstance().generateStructure(event.getWorld(),event.getRand(),new ChunkPos(event.getChunkX(),event.getChunkZ()));
    }

}
