package team.tnt.collectorsalbum.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class CommonLabels {

    public static final Component PASSED = Component.translatable("collectorsalbum.label.passed").withStyle(ChatFormatting.GREEN);
    public static final Component FAILED = Component.translatable("collectorsalbum.label.failed").withStyle(ChatFormatting.RED);
    public static final Component APPLIES = Component.translatable("collectorsalbum.label.applies");

    public static Component getBoolState(boolean value) {
        return value ? PASSED : FAILED;
    }

}
