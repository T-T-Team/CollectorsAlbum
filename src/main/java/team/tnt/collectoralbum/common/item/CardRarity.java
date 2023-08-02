package team.tnt.collectoralbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectoralbum.common.init.SoundRegistry;

import java.util.function.Supplier;

public enum CardRarity {

    COMMON(ChatFormatting.WHITE, SoundRegistry.FLIP_COMMON),
    UNCOMMON(ChatFormatting.GREEN, SoundRegistry.FLIP_UNCOMMON),
    RARE(ChatFormatting.BLUE, SoundRegistry.FLIP_RARE),
    EPIC(ChatFormatting.DARK_PURPLE, SoundRegistry.FLIP_EPIC),
    LEGENDARY(ChatFormatting.GOLD, SoundRegistry.FLIP_LEGENDARY),
    MYTHICAL(ChatFormatting.RED, SoundRegistry.FLIP_MYTHICAL);

    final int value;
    final ChatFormatting color;
    final Supplier<SoundEvent> discoverySound;
    final Component name;

    CardRarity(ChatFormatting color, Supplier<SoundEvent> discoverySound) {
        this.value = this.ordinal() + 1;
        this.color = color;
        this.discoverySound = discoverySound;
        this.name = Component.translatable("card.rarity." + name().toLowerCase()).withStyle(color);
    }

    public SoundEvent getDiscoverySound() {
        return discoverySound.get();
    }

    public int getValue() {
        return value;
    }

    public Component getTranslatedName() {
        return name;
    }
}
