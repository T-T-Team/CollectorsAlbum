package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

public class DropReferenceProvider implements ItemDropProvider {

    public static final MapCodec<DropReferenceProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("reference").forGetter(t -> t.reference)
    ).apply(instance, DropReferenceProvider::new));

    private final ResourceLocation reference;

    public DropReferenceProvider(ResourceLocation reference) {
        this.reference = reference;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        CardPackDropManager manager = CardPackDropManager.getInstance();
        ItemDropProvider provider = manager.getProvider(this.reference);
        provider.generateDrops(context, output);
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.REFERENCE.get();
    }
}
