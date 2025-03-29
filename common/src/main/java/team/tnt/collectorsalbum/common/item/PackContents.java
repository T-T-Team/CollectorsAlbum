package team.tnt.collectorsalbum.common.item;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PackContents(List<ItemStack> drops) {
    public static final Codec<PackContents> CODEC = ItemStack.CODEC.listOf().xmap(PackContents::new, contents -> contents.drops);

    public boolean isEmpty() {
        return this.drops.isEmpty();
    }
}
