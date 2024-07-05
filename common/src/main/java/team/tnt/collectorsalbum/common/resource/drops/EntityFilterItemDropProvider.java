package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;

public class EntityFilterItemDropProvider implements ItemDropProvider {

    public static final MapCodec<EntityFilterItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("filter").forGetter(t -> t.filter),
            ItemDropProviderType.INSTANCE_CODEC.fieldOf("pass").forGetter(t -> t.pass),
            ItemDropProviderType.INSTANCE_CODEC.optionalFieldOf("fail", NoItemDropProvider.INSTANCE).forGetter(t -> t.fail)
    ).apply(instance, EntityFilterItemDropProvider::new));

    private final TagKey<EntityType<?>> filter;
    private final ItemDropProvider pass;
    private final ItemDropProvider fail;

    public EntityFilterItemDropProvider(TagKey<EntityType<?>> filter, ItemDropProvider pass, ItemDropProvider fail) {
        this.filter = filter;
        this.pass = pass;
        this.fail = fail;
    }

    @Override
    public void generateDrops(DropContext context, DropOutputBuilder<ItemStack> output) {
        Entity entity = context.getNullable(DropContext.ENTITY, Entity.class);
        if (entity == null) {
            CollectorsAlbum.LOGGER.warn("Failed to generate item drop, 'entity' key is not found in drop context!");
            return;
        }
        EntityType<?> entityType = entity.getType();
        if (entityType.is(this.filter)) {
            this.pass.generateDrops(context, output);
        } else {
            this.fail.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.ENTITY_FILTER.get();
    }
}
