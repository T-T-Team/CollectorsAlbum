package team.tnt.collectorsalbum.integrations.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.RegistryTags;

import java.util.List;

public class TrinketsIntegrationFabric {

    public static AlbumLocatorResult findTrinketAlbum(Player player, Album album) {
        return TrinketsApi.getTrinketComponent(player).map(component -> {
            List<Tuple<SlotReference, ItemStack>> equipped = component.getEquipped(stack -> stack.is(RegistryTags.Items.ALBUM));
            for (Tuple<SlotReference, ItemStack> tuple : equipped) {
                ItemStack itemStack = tuple.getB();
                Album foundAlbum = itemStack.get(ItemDataComponentRegistry.ALBUM.get());
                if (album != null && album.test(foundAlbum)) {
                    return AlbumLocatorResult.found(itemStack, album, -1);
                }
                if (foundAlbum != null) {
                    return AlbumLocatorResult.found(itemStack, foundAlbum, -1);
                }
            }
            return AlbumLocatorResult.notFound();
        }).orElseGet(AlbumLocatorResult::notFound);
    }
}
