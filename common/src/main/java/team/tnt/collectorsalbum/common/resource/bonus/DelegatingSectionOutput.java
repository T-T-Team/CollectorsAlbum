package team.tnt.collectorsalbum.common.resource.bonus;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public record DelegatingSectionOutput(SectionOutput baseOutput, Consumer<Component> title, Consumer<Component> description) implements SectionOutput {

    @Override
    public void withTitle(Component text) {
        this.title.accept(text);
    }

    @Override
    public void withDescription(Component text) {
        this.description.accept(text);
    }

    @Override
    public void withAdditionalInfo(Component text) {
        this.description.accept(text);
    }

    @Override
    public void condition(boolean applicable, Component text) {
        this.baseOutput.condition(applicable, text);
    }
}
