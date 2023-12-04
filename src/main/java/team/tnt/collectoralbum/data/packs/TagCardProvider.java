package team.tnt.collectoralbum.data.packs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.util.JsonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TagCardProvider implements ICardDropProvider {

    private final TagKey<Item> tag;

    private TagCardProvider(TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public List<ItemStack> provideDrops() {
        Random random = new Random();
        List<Holder<Item>> holders = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holders::add);
        if (holders.isEmpty()) {
            CollectorsAlbum.LOGGER.error("Attempted to provide items from empty or undefined tag {}", tag.location());
            return Collections.emptyList();
        }
        Holder<Item> holderRef = holders.get(random.nextInt(holders.size()));
        ItemStack itemStack = new ItemStack(holderRef);
        return Collections.singletonList(itemStack);
    }

    public static class Serializer implements ICardDropSerializer<TagCardProvider> {

        @Override
        public TagCardProvider fromJson(JsonElement data) throws JsonParseException {
            JsonObject object = JsonHelper.asObject(data);
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(object, "tag"));
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
            return new TagCardProvider(tagKey);
        }
    }
}
