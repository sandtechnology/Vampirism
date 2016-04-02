package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;


public class PotionThirst extends VampirismPotion {
    public PotionThirst(ResourceLocation location, boolean badEffect, int potionColor) {
        super(location, badEffect, potionColor);
        setIconIndex(1, 1).setPotionName("potion.vampirism.thirst");
        registerPotionAttributeModifier(VReference.bloodExhaustion, "f6d9889e-dfdc-11e5-b86d-9a79f06e9478", 0.5F, 1);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            VampirePlayer.get((EntityPlayer) entity).addExhaustion(0.010F * (amplifier + 1));
        }
    }
}