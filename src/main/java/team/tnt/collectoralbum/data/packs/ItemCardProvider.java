package team.tnt.collectoralbum.data.packs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import team.tnt.collectoralbum.util.JsonHelper;

import java.util.Collections;
import java.util.List;

public class ItemCardProvider implements ICardDropProvider {

    private final Item item;

    private ItemCardProvider(Item item) {
        this.item = item;
    }

    @Override
    public List<ItemStack> provideDrops() {
        ItemStack itemStack = new ItemStack(item);
        return Collections.singletonList(itemStack);
    }

    public static class Serializer implements ICardDropSerializer<ItemCardProvider> {

        @Override
        public ItemCardProvider fromJson(JsonElement data) throws JsonParseException {
            JsonObject object = JsonHelper.asObject(data);
            ResourceLocation identifier = new ResourceLocation(GsonHelper.getAsString(object, "item"));
            Item item = BuiltInRegistries.ITEM.get(identifier);
            if (item == Items.AIR) {
                throw new JsonSyntaxException("Unknown item: " + identifier);
            }
            return new ItemCardProvider(item);
        }
    }
}
