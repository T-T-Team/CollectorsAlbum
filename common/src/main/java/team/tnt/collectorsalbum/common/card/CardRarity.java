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

    COMMON(new Integer[] {0xAAAAAA}, new Integer[] {5}, SoundRegistry.FLIP_COMMON, text -> text.withStyle(ChatFormatting.WHITE)),
    UNCOMMON(new Integer[] {0x55ff55}, new Integer[] {5}, SoundRegistry.FLIP_UNCOMMON, text -> text.withStyle(ChatFormatting.GREEN)),
    RARE(new Integer[] {0x5555ff}, new Integer[] {5}, SoundRegistry.FLIP_RARE, text -> text.withStyle(ChatFormatting.BLUE)),
    EPIC(new Integer[] {0xaa00aa}, new Integer[] {5}, SoundRegistry.FLIP_EPIC, text -> text.withStyle(ChatFormatting.DARK_PURPLE)),
    LEGENDARY(new Integer[] {0xffaa00}, new Integer[] {5}, SoundRegistry.FLIP_LEGENDARY, text -> text.withStyle(ChatFormatting.GOLD)),
    MYTHICAL(new Integer[] {0xff5555, 0xffffff}, new Integer[] {5, 10}, SoundRegistry.FLIP_MYTHICAL, text -> text.withStyle(ChatFormatting.RED)),;

    private final Integer[] colors;
    private final Integer[] durations;
    private final Supplier<SoundEvent> flipSound;
    private final Component text;
    private final int value;

    CardRarity(Integer[] colors, Integer[] durations, Supplier<SoundEvent> flipSound, UnaryOperator<MutableComponent> displayTextFormatter) {
        this.colors = colors;
        this.durations = durations;
        this.flipSound = flipSound;
        this.text = displayTextFormatter.apply(Component.translatable("card.rarity." + name().toLowerCase(Locale.ROOT)));
        this.value = ordinal() + 1;
    }

    public Integer[] getColors() {
        return colors;
    }

    public Integer[] getDurations() {
        return durations;
    }

    public Supplier<SoundEvent> getFlipSoundRef() {
        return this.flipSound;
    }

    public Component getDisplayText() {
        return this.text;
    }

    public int getValue() {
        return this.value;
    }
}
