package team.tnt.collectorsalbum.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import team.tnt.collectorsalbum.common.resource.bonus.IntermediateAlbumBonus;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AlbumBonusDescriptionOutput {

    private final ActionContext context;
    private final List<DescriptionLabel> holders = new ArrayList<>();
    private int currentLevel;

    private AlbumBonusDescriptionOutput(ActionContext context) {
        this.context = context;
    }

    public static ChatFormatting getBooleanColor(boolean value) {
        return value ? ChatFormatting.GREEN : ChatFormatting.RED;
    }

    public ActionContext getContext() {
        return context;
    }

    public void condition(Component label, Component tooltip, boolean applicable, IntermediateAlbumBonus bonus) {
        ChatFormatting color = getBooleanColor(applicable);
        MutableComponent component = Component.literal(label.getString()).withStyle(color);
        this.text(component, tooltip);
        this.nested(() -> bonus.children().forEach(child -> child.addDescription(this)));
    }

    public void condition(Component label, Component tooltip, IntermediateAlbumBonus bonus) {
        this.condition(label, tooltip, bonus.canApply(this.context), bonus);
    }

    public void condition(Component label, boolean applicable, IntermediateAlbumBonus bonus) {
        this.condition(label, null, applicable, bonus);
    }

    public void condition(Component label, IntermediateAlbumBonus bonus) {
        this.condition(label, null, bonus);
    }

    public void list(Component label, Component tooltip, IntermediateAlbumBonus bonus) {
        this.text(label, tooltip);
        this.nested(() -> bonus.children().forEach(child -> child.addDescription(this)));
    }

    public void list(Component label, IntermediateAlbumBonus bonus) {
        this.list(label, null, bonus);
    }

    public void text(Component label, Component tooltip) {
        this.generic(new Label(label, tooltip, this.currentLevel));
    }

    public void text(Component label) {
        this.text(label, null);
    }

    public void generic(DescriptionLabel holder) {
        this.holders.add(holder);
    }

    public void nested(Runnable action) {
        ++this.currentLevel;
        action.run();
        --this.currentLevel;
    }

    public static AlbumBonusDescriptionOutput createEmpty(ActionContext context) {
        return new AlbumBonusDescriptionOutput(context);
    }

    public List<ComponentWithTooltip> toComponentList() {
        return this.holders.stream().flatMap(l -> l.components().stream()).toList();
    }

    public record ComponentWithTooltip(Component label, Component tooltip, int level) {

        public boolean hasTooltip() {
            return this.tooltip != null;
        }
    }

    public interface DescriptionLabel {
        List<ComponentWithTooltip> components();
    }

    private record Label(Component label, Component tooltip, int level) implements DescriptionLabel {

        @Override
        public List<ComponentWithTooltip> components() {
            ComponentWithTooltip component = new ComponentWithTooltip(label, tooltip, level);
            return Collections.singletonList(component);
        }
    }
}
