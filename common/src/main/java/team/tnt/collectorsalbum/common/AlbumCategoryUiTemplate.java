package team.tnt.collectorsalbum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.Codecs;

public final class AlbumCategoryUiTemplate {

    public static final ResourceLocation DEFAULT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/album_category.png");
    public static final ResourceLocation DEFAULT_SLOT = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/album_slot.png");
    public static final Codec<AlbumCategoryUiTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextureTemplate.CODEC.optionalFieldOf("backgroundTexture", TextureTemplate.ALBUM_BG).forGetter(t -> t.backgroundTexture),
            TextureTemplate.CODEC.optionalFieldOf("slotTexture", TextureTemplate.SLOT_BG).forGetter(t -> t.slotTexture),
            Codec.BOOL.optionalFieldOf("renderSlots", true).forGetter(t -> t.renderSlots),
            Codec.BOOL.optionalFieldOf("renderSlotCardNumbers", true).forGetter(t -> t.renderSlotCardNumbers),
            Codecs.COLOR_CODEC.optionalFieldOf("slotCardNumberTextColor", 0x7B5C4C).forGetter(t -> t.slotCardNumberTextColor),
            SlotPositionTemplate.CODEC.optionalFieldOf("slotPositions", SlotPositionTemplate.TEMPLATE).forGetter(t -> t.slotTemplate),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("bookmarkItem", Items.AIR).xmap(Item::getDefaultInstance, ItemStack::getItem).forGetter(t -> t.bookmarkIcon)
    ).apply(instance, AlbumCategoryUiTemplate::new));
    public static final AlbumCategoryUiTemplate DEFAULT_TEMPLATE = new AlbumCategoryUiTemplate(
            TextureTemplate.ALBUM_BG, TextureTemplate.SLOT_BG, true, true, 0x7A3499,
            SlotPositionTemplate.TEMPLATE, ItemStack.EMPTY
    );

    public final TextureTemplate backgroundTexture;
    public final TextureTemplate slotTexture;
    public final boolean renderSlots;
    public final boolean renderSlotCardNumbers;
    public final int slotCardNumberTextColor;
    public final SlotPositionTemplate slotTemplate;
    public final ItemStack bookmarkIcon;

    public AlbumCategoryUiTemplate(TextureTemplate backgroundTexture, TextureTemplate slotTexture, boolean renderSlots, boolean renderSlotCardNumbers, int slotCardNumbersTextColor, SlotPositionTemplate template, ItemStack bookmarkIcon) {
        this.backgroundTexture = backgroundTexture;
        this.slotTexture = slotTexture;
        this.renderSlots = renderSlots;
        this.renderSlotCardNumbers = renderSlotCardNumbers;
        this.slotCardNumberTextColor = slotCardNumbersTextColor;
        this.slotTemplate = template;
        this.bookmarkIcon = bookmarkIcon;
    }

    public record TextureTemplate(ResourceLocation resource, int width, int height, int texU, int texV, int textureWidth, int textureHeight) {
        public static final TextureTemplate ALBUM_BG = new TextureTemplate(DEFAULT_BACKGROUND, 256, 256, 0, 0, 256, 256);
        public static final TextureTemplate SLOT_BG = new TextureTemplate(DEFAULT_SLOT, 18, 18, 0, 0, 18, 18);
        public static final Codec<TextureTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("resource").forGetter(TextureTemplate::resource),
                Codec.INT.optionalFieldOf("imageWidth", 256).forGetter(TextureTemplate::width),
                Codec.INT.optionalFieldOf("imageHeight", 256).forGetter(TextureTemplate::height),
                Codec.INT.optionalFieldOf("u", 0).forGetter(TextureTemplate::texU),
                Codec.INT.optionalFieldOf("v", 0).forGetter(TextureTemplate::texV),
                Codec.INT.optionalFieldOf("textureWidth", 256).forGetter(TextureTemplate::textureWidth),
                Codec.INT.optionalFieldOf("textureHeight", 256).forGetter(TextureTemplate::textureHeight)
        ).apply(instance, TextureTemplate::new));
    }

    public record SlotPositionTemplate(int xAlbumSlotPositionStart, int yAlbumSlotPositionStart, int xSlotSpacing, int ySlotSpacing, int xPlayerSlotPositionStart, int yPlayerSlotPositionStart, int pageWidth, int colums, int rows) {
        public static final SlotPositionTemplate TEMPLATE = new SlotPositionTemplate(25, 19, 30, 30, 47, 173, 128, 3, 5);
        public static final Codec<SlotPositionTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("xAlbumSlotStart", 25).forGetter(SlotPositionTemplate::xAlbumSlotPositionStart),
                Codec.INT.optionalFieldOf("yAlbumSlotStart", 19).forGetter(SlotPositionTemplate::yAlbumSlotPositionStart),
                Codec.INT.optionalFieldOf("xSlotSpacing", 30).forGetter(SlotPositionTemplate::xSlotSpacing),
                Codec.INT.optionalFieldOf("ySlotSpacing", 30).forGetter(SlotPositionTemplate::ySlotSpacing),
                Codec.INT.optionalFieldOf("xPlayerSlotStart", 47).forGetter(SlotPositionTemplate::xPlayerSlotPositionStart),
                Codec.INT.optionalFieldOf("yPlayerSlotStart", 173).forGetter(SlotPositionTemplate::yPlayerSlotPositionStart),
                Codec.INT.optionalFieldOf("pageWidth", 128).forGetter(SlotPositionTemplate::pageWidth),
                Codec.INT.optionalFieldOf("colums", 3).forGetter(SlotPositionTemplate::colums),
                Codec.INT.optionalFieldOf("rows", 5).forGetter(SlotPositionTemplate::rows)
        ).apply(instance, SlotPositionTemplate::new));
    }
}
