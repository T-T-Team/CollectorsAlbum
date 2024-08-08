package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

public class MonsterEntityFilterItemDropProvider implements ItemDropProvider {

    public static final MapCodec<MonsterEntityFilterItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemDropProviderType.INSTANCE_CODEC.fieldOf("pass").forGetter(t -> t.pass),
            ItemDropProviderType.INSTANCE_CODEC.optionalFieldOf("fail", NoItemDropProvider.INSTANCE).forGetter(t -> t.fail)
    ).apply(instance, MonsterEntityFilterItemDropProvider::new));

    private final ItemDropProvider pass;
    private final ItemDropProvider fail;

    public MonsterEntityFilterItemDropProvider(ItemDropProvider pass, ItemDropProvider fail) {
        this.pass = pass;
        this.fail = fail;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        Entity entity = context.getNullable(ActionContext.ENTITY, Entity.class);
        if (entity instanceof Monster) {
            this.pass.generateDrops(context, output);
        } else {
            this.fail.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.MONSTER_ENTITY_FILTER.get();
    }
}
