package team.tnt.collectoralbum.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class LegacyAlbumDataHelper {

    /*
    for (ICardCategory category : CardCategoryRegistry.getValues()) {
            ListTag slots = inventories.getList(category.getId().toString(), Tag.TAG_COMPOUND);
            for (int i = 0; i < slots.size(); i++) {
                CompoundTag slotDef = slots.getCompound(i);
                int slotIndex = slotDef.getInt("slotIndex");
                CompoundTag itemTag = slotDef.getCompound("itemStack");
                ItemStack item = ItemStack.of(itemTag);
                inventoriesByCategory.get(category).setItem(slotIndex, item);
            }
        }
     */
    public void migrateNbt(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("inventories")) {
            CompoundTag inventories = tag.getCompound("inventories");
        }
    }
}
