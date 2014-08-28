package de.teamlapen.vampirism.playervampire;

import java.util.ArrayList;
import java.util.List;

import scala.reflect.internal.Trees.This;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.playervampire.PlayerModifiers.Modifier;
import de.teamlapen.vampirism.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class VampirePlayer implements IExtendedEntityProperties {
	
	public final static String EXT_PROP_NAME = "VampirePlayer";
	private final EntityPlayer player;
	private int level;
	private final String KEY_LEVEL="level";
	private List<Modifier> modifiers;
	
	public VampirePlayer(EntityPlayer player){
		this.player=player;
		level=0;
		modifiers=new ArrayList<Modifier>();
	}
	
	private static final String getSaveKey(EntityPlayer player) {
		// no longer a username field, so use the command sender name instead:
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}
	
	public static final void loadProxyData(EntityPlayer player){
		VampirePlayer playerData=VampirePlayer.get(player);
		NBTTagCompound savedData = CommonProxy.getEntityData(getSaveKey(player));
		if(savedData!=null){
			playerData.loadNBTData(savedData);
			
		}
		playerData.sync();
		playerData.applyModifiers();
	}
	
	public static void saveProxyData(EntityPlayer player){
		VampirePlayer playerData=VampirePlayer.get(player);
		NBTTagCompound savedData=new NBTTagCompound();
		playerData.saveNBTData(savedData);
		CommonProxy.storeEntityData(getSaveKey(player),savedData);
	}
	
	/**
	 * Registers vampire property to player
	 * @param player
	 */
	public static final void register(EntityPlayer player){
		player.registerExtendedProperties(VampirePlayer.EXT_PROP_NAME, new VampirePlayer(player));
	}
	
	/**
	 * 
	 * @param player
	 * @return VampirePlayer property of player
	 */
	public static final VampirePlayer get(EntityPlayer player){
		return (VampirePlayer) player.getExtendedProperties(VampirePlayer.EXT_PROP_NAME);
	}


	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_LEVEL, this.level);
		
		compound.setTag(EXT_PROP_NAME, properties);
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		this.level=properties.getInteger(KEY_LEVEL);
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
	
	public void levelUp(){
		level++;
		this.sync();
		this.applyModifiers();
	}
	
	public int getLevel(){
		return level;
	}
	
	private void sync(){
		NBTTagCompound nbt=new NBTTagCompound();
		nbt.setDouble("posx", player.posX);
		nbt.setDouble("posy", player.posY);
		nbt.setDouble("posZ", player.posZ);
		this.saveNBTData(nbt);
		VampirismMod.modChannel.sendTo(new VampirePlayerPacket(nbt), (EntityPlayerMP)player);
		
	}
	

	private void applyModifiers() {
		PlayerModifiers.applyModifiers(this, modifiers,player);
		
	}

}