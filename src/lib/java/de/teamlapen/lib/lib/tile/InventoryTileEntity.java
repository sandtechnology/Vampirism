package de.teamlapen.lib.lib.tile;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;


/**
 * Basic abstract class for TileEntitys which need a small inventory (with an gui)
 */
public abstract class InventoryTileEntity extends TileEntity implements IInventory, InventorySlot.IInventorySlotInventory {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 40;
    private InventorySlot[] slots;

    /**
     * @param slots A slot 'description'. The array should contain one Slot instance for each inventory slot which should be created. The slots must each contain the position where they should be
     *              displayed in the GUI, they can also contain a filter for which items are allowed. Make sure that these Slot instance are unique on server and client.
     */
    public InventoryTileEntity(InventorySlot[] slots) {
        this.slots = slots;
    }

    @Override
    public void clear() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return ItemStackUtil.decrIInventoryStackSize(this, slot, amt);
    }

    @Nonnull
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public Container getNewInventoryContainer(InventoryPlayer inv) {
        return new InventoryContainer(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return slots.length;
    }

    @Override
    public InventorySlot[] getSlots() {
        return slots;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slots[slot].stack;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        for (InventorySlot slot : slots) {
            if (!slot.stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slots[slot].itemSelector != null && !ItemStackUtil.isEmpty(stack)) {
            return slots[slot].itemSelector.isItemAllowed(stack);
        }
        return true;
    }


    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(getPos()) < MAX_DIST_SQRT;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        for (InventorySlot slot1 : slots) {
            slot1.stack = ItemStackUtil.getEmptyStack();
        }
        NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < slots.length) {
                slots[slot].stack = ItemStackUtil.loadFromNBT(tag);
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.slots[index] != null) {
            ItemStack itemstack = this.slots[index].stack;
            this.slots[index].stack = ItemStackUtil.getEmptyStack();
            return itemstack;
        } else {
            return ItemStackUtil.getEmptyStack();
        }
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        slots[slot].stack = stack;
        if (ItemStackUtil.getCount(stack) > getInventoryStackLimit()) {
            ItemStackUtil.setCount(stack, getInventoryStackLimit());
        }
        this.markDirty();//Not sure

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i].stack;
            if (!ItemStackUtil.isEmpty(stack)) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }
        nbt.setTag("Inventory", itemList);

        return nbt;
    }


    protected boolean isFull() {
        for (InventorySlot s : this.getSlots()) {
            if (this.slots[0].stack.isEmpty() || this.slots[0].stack.getCount() < this.slots[0].stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

}