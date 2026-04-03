package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public class EntityFilterItemDropProvider implements ItemDropProvider {

    public static final MapCodec<EntityFilterItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.ENTITY_TYPE).fieldOf("filter").forGetter(t -> t.filter),
            ItemDropProviderType.INSTANCE_CODEC.fieldOf("pass").forGetter(t -> t.pass),
            ItemDropProviderType.INSTANCE_CODEC.optionalFieldOf("fail", NoItemDropProvider.INSTANCE).forGetter(t -> t.fail),
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(t -> t.blacklist)
    ).apply(instance, EntityFilterItemDropProvider::new));

    private final TagKey<EntityType<?>> filter;
    private final ItemDropProvider pass;
    private final ItemDropProvider fail;
    private final boolean blacklist;

    public EntityFilterItemDropProvider(TagKey<EntityType<?>> filter, ItemDropProvider pass, ItemDropProvider fail, boolean blacklist) {
        this.filter = filter;
        this.pass = pass;
        this.fail = fail;
        this.blacklist = blacklist;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        Entity entity = context.getNullable(ActionContext.ENTITY, Entity.class);
        if (entity == null) {
            CollectorsAlbum.LOGGER.warn("Failed to generate item drop, 'entity' key is not found in drop context!");
            return;
        }
        ItemDropProvider pass = this.blacklist ? this.fail : this.pass;
        ItemDropProvider fail = this.blacklist ? this.pass : this.fail;
        if (entity.is(this.filter)) {
            pass.generateDrops(context, output);
        } else {
            fail.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.ENTITY_FILTER.get();
    }

    @Override
    public Stream<ItemStackTemplate> view() {
        return Stream.empty();
    }
}
