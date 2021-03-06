package de.teamlapen.vampirism.network;

import de.teamlapen.lib.lib.network.AbstractMessageHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Extend this to easily handle messages on server side
 */
public abstract class AbstractServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>

{

    public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
        return null;
    }

    @Override
    protected AbstractPacketDispatcher getDispatcher() {
        return VampirismMod.dispatcher;
    }
}