package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Optional;
import java.util.function.Function;

public record AttributeAlbumBonus(Holder<Attribute> attribute, AttributeModifier attributeModifier, Optional<Component> label, boolean contextual) implements AlbumBonus {

    public static final Codec<AttributeModifier> CONFIGURABLE_MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AttributeModifier::id),
            Codec.either(Codec.DOUBLE, NumberProviderType.INSTANCE_CODEC).fieldOf("amount").forGetter(t -> Either.left(t.amount())),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)
    ).apply(instance, (id, either, op) -> new AttributeModifier(id, either.map(Function.identity(), NumberProvider::doubleValue), op)));
    public static final MapCodec<AttributeAlbumBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(t -> t.attribute),
            CONFIGURABLE_MODIFIER_CODEC.fieldOf("modifier").forGetter(t -> t.attributeModifier),
            ComponentSerialization.CODEC.optionalFieldOf("label").forGetter(t -> t.label),
            Codec.BOOL.optionalFieldOf("contextual", false).forGetter(t -> t.contextual)
    ).apply(instance, AttributeAlbumBonus::new));

    @Override
    public void appendDetails(SectionOutput writer, ActionContext ctx) {
        Attribute unwrappedAttribute = this.attribute.value();
        Component attributeDisplay = Component.translatable(unwrappedAttribute.getDescriptionId());

        writer.withTitle(attributeDisplay);
        writer.withDescription(this.getLabel());
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

    private Component getLabel() {
        if (this.label.isEmpty()) {
            return Component.translatable("collectorsalbum.label.bonus.attribute_bonus.detail", this.attributeModifier.amount(), this.attributeModifier.operation().name());
        }
        Component customLabel = this.label.get();
        if (this.contextual && customLabel.getContents() instanceof TranslatableContents translatableContents) {
            String key = translatableContents.getKey();
            return Component.translatable(key, this.attributeModifier.amount()).setStyle(customLabel.getStyle());
        }
        return customLabel;
    }
}
