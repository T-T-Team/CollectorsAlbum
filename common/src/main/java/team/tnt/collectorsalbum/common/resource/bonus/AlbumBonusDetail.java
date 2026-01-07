package team.tnt.collectorsalbum.common.resource.bonus;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record AlbumBonusDetail(BonusHolder bonus, List<Component> additionalInfo, List<Section> sections) {

    public Component title() {
        return this.bonus.title();
    }

    public Component description() {
        return this.bonus.description();
    }

    public boolean hasAdditionalInfo() {
        return !this.additionalInfo.isEmpty();
    }

    public static final class Output implements SectionOutput {

        private final BonusHolder holder;
        private final List<Component> additionalInfo = new ArrayList<>();
        private final List<Section> sections = new ArrayList<>();

        public Output(BonusHolder holder) {
            this.holder = holder;
        }

        @Override
        public void withTitle(Component text) {
        }

        @Override
        public void withDescription(Component text) {
            this.additionalInfo.add(text);
        }

        @Override
        public void withAdditionalInfo(Component text) {
            this.withDescription(text);
        }

        @Override
        public void condition(boolean applicable, Component text) {
        }

        public void section(Consumer<SectionOutput> sectionOutput) {
            SectionOutputWriter writer = new SectionOutputWriter(this);
            sectionOutput.accept(writer);
            this.sections.add(writer.buildSection());
        }

        public AlbumBonusDetail build() {
            return new AlbumBonusDetail(this.holder, this.additionalInfo, this.sections);
        }
    }

    private static final class SectionOutputWriter implements SectionOutput {

        private final Output output;
        private Component title;
        private final List<Component> description;
        private final List<ConditionDetail> conditions;

        SectionOutputWriter(Output output) {
            this.output = output;
            this.title = CommonComponents.EMPTY;
            this.description = new ArrayList<>();
            this.conditions = new ArrayList<>();
        }

        @Override
        public void withTitle(Component text) {
            this.title = text;
        }

        @Override
        public void withDescription(Component text) {
            this.description.add(text);
        }

        @Override
        public void withAdditionalInfo(Component text) {
            this.output.withDescription(text);
        }

        @Override
        public void condition(boolean applicable, Component text) {
            this.conditions.add(new ConditionDetail(applicable, text));
        }

        public Section buildSection() {
            return new Section(this.title, this.description, this.conditions);
        }
    }

    public record Section(Component title, List<Component> description, List<ConditionDetail> conditions) {
    }

    public record ConditionDetail(boolean fulfilled, Component title) {
    }
}
