package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityLivingBase;

/**
 * Interface for the player vampire data.
 * Attached to all players as capability
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer<IVampirePlayer>, IMinionLord, IBiteableEntity {

    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(EntityLivingBase entity);

    /**
     * @return The players vampire skill handler
     */
    IActionHandler<IVampirePlayer> getActionHandler();

    int getBloodLevel();

    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100
     */
    int getTicksInSun();

    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();

    boolean isVampireLord();


    enum BITE_TYPE {
        ATTACK, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD, NONE
    }
}