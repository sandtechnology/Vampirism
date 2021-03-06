package de.teamlapen.lib.proxy;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.lib.util.ParticleHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;


public class CommonProxy implements IProxy {

    final ParticleHandler serverParticleHandler = new ParticleHandlerServer();//Not required on client side, but since on an integrated server only client proxy exist we need it here

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, BlockPos pos, float volume, float pinch) {
        VampLib.log.w("ISoundReference", "Created sound reference server side. Nothing will happen");
        return new ISoundReference.Dummy();
    }

    @Nonnull
    @Override
    public ISoundReference createSoundReference(SoundEvent event, SoundCategory category, double x, double y, double z, float volume, float pinch) {
        VampLib.log.w("ISoundReference", "Created sound reference server side. Nothing will happen");
        return new ISoundReference.Dummy();
    }

    @Override
    public String getActiveLanguage() {
        return "English";
    }

    @Override
    public ParticleHandler getParticleHandler() {
        return serverParticleHandler;
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(WordUtils.wrap(str, wrapWidth / 6, "\n", false).split("\n"));
    }
}
