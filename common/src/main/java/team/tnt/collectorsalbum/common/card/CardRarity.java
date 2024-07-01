package team.tnt.collectorsalbum.common.card;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectorsalbum.common.init.SoundRegistry;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public enum CardRarity {

    COMMON(SoundRegistry.FLIP_COMMON, text -> text.withStyle(ChatFormatting.WHITE)),
    UNCOMMON(SoundRegistry.FLIP_UNCOMMON, text -> text.withStyle(ChatFormatting.GREEN)),
    RARE(SoundRegistry.FLIP_RARE, text -> text.withStyle(ChatFormatting.BLUE)),
    EPIC(SoundRegistry.FLIP_EPIC, text -> text.withStyle(ChatFormatting.DARK_PURPLE)),
    LEGENDARY(SoundRegistry.FLIP_LEGENDARY, text -> text.withStyle(ChatFormatting.GOLD)),
    MYTHICAL(SoundRegistry.FLIP_MYTHICAL, text -> text.withStyle(ChatFormatting.RED)),;

    private final Supplier<SoundEvent> flipSound;
    private final Component text;
    private final int value;

    CardRarity(Supplier<SoundEvent> flipSound, UnaryOperator<MutableComponent> displayTextFormatter) {
        this.flipSound = flipSound;
        this.text = displayTextFormatter.apply(Component.translatable("card.rarity." + name().toLowerCase(Locale.ROOT)));
        this.value = ordinal() + 1;
    }

    public SoundEvent getFlipSound() {
        return this.flipSound.get();
    }

    public Component getDisplayText() {
        return this.text;
    }

    public int getValue() {
        return this.value;
    }
}
