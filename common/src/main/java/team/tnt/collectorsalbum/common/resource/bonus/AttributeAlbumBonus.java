package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.function.Function;

public class AttributeAlbumBonus implements AlbumBonus {

    public static final Codec<AttributeModifier> CONFIGURABLE_MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifier::id),
            Codec.either(Codec.DOUBLE, NumberProviderType.INSTANCE_CODEC).fieldOf("amount").forGetter(t -> Either.left(t.amount())),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)
    ).apply(instance, (id, either, op) -> new AttributeModifier(id, either.map(Function.identity(), NumberProvider::doubleValue), op)));
    public static final MapCodec<AttributeAlbumBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(t -> t.attribute),
                CONFIGURABLE_MODIFIER_CODEC.fieldOf("modifier").forGetter(t -> t.attributeModifier)
    ).apply(instance, AttributeAlbumBonus::new));
    private static final String LABEL_ATTRIBUTE = "collectorsalbum.label.bonus.attribute";
    private static final String LABEL_ATTRIBUTE_MODIFIER = "collectorsalbum.label.bonus.attribute_modifier";
    private static final String TOOL_ATTRIBUTE_OPERATION = "collectorsalbum.tooltip.bonus.attribute_operation";

    private final Holder<Attribute> attribute;
    private final AttributeModifier attributeModifier;

    public AttributeAlbumBonus(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
        this.attribute = attribute;
        this.attributeModifier = attributeModifier;
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        Attribute unwrappedAttribute = this.attribute.value();
        Component attributeName = Component.translatable(unwrappedAttribute.getDescriptionId()).withStyle(unwrappedAttribute.getStyle(true));
        description.text(Component.translatable(LABEL_ATTRIBUTE, attributeName));
        description.text(
                Component.translatable(LABEL_ATTRIBUTE_MODIFIER, Component.literal(String.valueOf(this.attributeModifier.amount())).withStyle(ChatFormatting.GREEN)),
                Component.translatable(TOOL_ATTRIBUTE_OPERATION, Component.literal(this.attributeModifier.operation().name()))
        );
    }

    @Override
    public void apply(ActionContext context) {
        Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
        AttributeInstance instance = player.getAttribute(this.attribute);
        if (instance != null) {
            AttributeModifier activeModifier = instance.getModifier(this.attributeModifier.id());
            if (activeModifier == null || !activeModifier.equals(this.attributeModifier)) {
                instance.removeModifier(this.attributeModifier); // Needs to be deleted as vanilla does not check for equality when replacing
                instance.addPermanentModifier(this.attributeModifier);
            }
        }
    }

    @Override
    public void removed(ActionContext context) {
        Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
        AttributeInstance instance = player.getAttribute(this.attribute);
        if (instance != null) {
            AttributeModifier modifier = instance.getModifier(this.attributeModifier.id());
            if (this.attributeModifier.equals(modifier)) {
                instance.removeModifier(this.attributeModifier);
            }
        }
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.ATTRIBUTE.get();
    }
}
