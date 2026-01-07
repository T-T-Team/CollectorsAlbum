package team.tnt.collectorsalbum.common.resource.bonus;

import net.minecraft.network.chat.Component;

public interface SectionOutput {

    void withTitle(Component text);

    void withDescription(Component text);

    void withAdditionalInfo(Component text);

    void condition(boolean applicable, Component text);
}
