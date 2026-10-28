package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.item.AlbumItem;
import team.tnt.collectorsalbum.platform.registration.Reference;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public final class ItemRegistry {

    public static Reference<AlbumItem> ALBUM;

    public static void init(RegistrationManager<Item> registry) {
        ItemRegistryHelper helper = new ItemRegistryHelper(registry);
        List<String> cards = getDefaultCards();

        ALBUM = helper.registerItem("album", props -> new AlbumItem(props.stacksTo(1).rarity(Rarity.RARE).component(ItemDataComponentRegistry.ALBUM.get(), Album.emptyAlbum())));

        List<String> sets = List.of("common", "uncommon", "rare", "epic", "legendary", "mythical");
        registerCardPacks(helper, sets);
        registerCardSets(cards, helper, sets);
        registerBlankCards(helper, 90);
    }

    private static void registerBlankCards(ItemRegistryHelper helper, int count) {
        for (int i = 0; i < count; i++) {
            helper.registerItem("blank_card_" + (i + 1), Item::new);
        }
    }

    private static void registerCardSets(Collection<String> cardSet, ItemRegistryHelper helper, Collection<String> sets) {
        for (String card : cardSet) {
            for (String set : sets) {
                String cardId = set + "_" + card;
                helper.registerItem(cardId, Item::new);
            }
        }
    }

    private static void registerCardPacks(ItemRegistryHelper helper, Collection<String> sets) {
        List<String> repackedPacks = new ArrayList<>();
        List<String> customPacks = new ArrayList<>();
        for (String set : sets) {
            String id = set + "_card_pack";
            helper.registerItem(id, props -> createPackDropsItem(helper.getNamespace(), id, props));
            repackedPacks.add(set + "_repacked_card_pack");
            customPacks.add(set + "_custom_card_pack");
        }
        // register at the end for better ordering
        repackedPacks.forEach(pack -> helper.registerItem(pack, props -> createPackDropsItem(helper.getNamespace(), pack, props)));
        customPacks.forEach(pack -> helper.registerItem(pack, Item::new));
    }

    private static Item createPackDropsItem(String namespace, String id, Item.Properties properties) {
        return new Item(properties.component(ItemDataComponentRegistry.PACK_DROPS_TABLE.get(), Identifier.fromNamespaceAndPath(namespace, id)));
    }

    private static List<String> getDefaultCards() {
        return List.of(
                "wooden_sword_card",
                "wooden_pickaxe_card",
                "wooden_axe_card",
                "wooden_shovel_card",
                "wooden_hoe_card",
                "stone_sword_card",
                "stone_pickaxe_card",
                "stone_axe_card",
                "stone_shovel_card",
                "stone_hoe_card",
                "iron_sword_card",
                "iron_pickaxe_card",
                "iron_axe_card",
                "iron_shovel_card",
                "iron_hoe_card",
                "golden_sword_card",
                "golden_pickaxe_card",
                "golden_axe_card",
                "golden_shovel_card",
                "golden_hoe_card",
                "diamond_sword_card",
                "diamond_pickaxe_card",
                "diamond_axe_card",
                "diamond_shovel_card",
                "diamond_hoe_card",
                "netherite_sword_card",
                "netherite_pickaxe_card",
                "netherite_axe_card",
                "netherite_shovel_card",
                "netherite_hoe_card",

                "leather_helmet_card",
                "leather_chestplate_card",
                "leather_leggings_card",
                "leather_boots_card",
                "iron_helmet_card",
                "iron_chestplate_card",
                "iron_leggings_card",
                "iron_boots_card",
                "chainmail_helmet_card",
                "chainmail_chestplate_card",
                "chainmail_leggings_card",
                "chainmail_boots_card",
                "golden_helmet_card",
                "golden_chestplate_card",
                "golden_leggings_card",
                "golden_boots_card",
                "diamond_helmet_card",
                "diamond_chestplate_card",
                "diamond_leggings_card",
                "diamond_boots_card",
                "netherite_helmet_card",
                "netherite_chestplate_card",
                "netherite_leggings_card",
                "netherite_boots_card",
                "leather_horse_armor_card",
                "iron_horse_armor_card",
                "golden_horse_armor_card",
                "diamond_horse_armor_card",
                "shield_card",
                "turtle_helmet_card",

                "blaze_card",
                "zombie_card",
                "skeleton_card",
                "creeper_card",
                "spider_card",
                "slime_card",
                "witch_card",
                "piglin_brute_card",
                "pillager_card",
                "wither_skeleton_card",
                "magma_cube_card",
                "ghast_card",
                "ender_dragon_card",
                "wither_card",
                "enderman_card",
                "bee_card",
                "panda_card",
                "cow_card",
                "chicken_card",
                "pig_card",
                "fox_card",
                "horse_card",
                "parrot_card",
                "sheep_card",
                "squid_card",
                "cat_card",
                "bat_card",
                "wolf_card",
                "villager_card",
                "polar_bear_card",

                "oak_sapling_card",
                "spruce_sapling_card",
                "birch_sapling_card",
                "jungle_sapling_card",
                "acacia_sapling_card",
                "dark_oak_sapling_card",
                "dead_bush_card",
                "dandelion_card",
                "poppy_card",
                "blue_orchid_card",
                "allium_card",
                "azure_bluet_card",
                "red_tulip_card",
                "oxeye_daisy_card",
                "brown_mushroom_card",
                "red_mushroom_card",
                "wheat_card",
                "sugar_cane_card",
                "apple_card",
                "golden_apple_card",
                "melon_slice_card",
                "carrot_card",
                "potato_card",
                "beetroot_card",
                "golden_carrot_card",
                "kelp_card",
                "bamboo_card",
                "sweet_berries_card",
                "glow_berries_card",
                "nether_wart_card",

                "enchantment_table_card",
                "beacon_card",
                "brewing_stand_card",
                "coal_card",
                "iron_ingot_card",
                "gold_ingot_card",
                "diamond_card",
                "emerald_card",
                "nether_star_card",
                "redstone_card",
                "glowstone_dust_card",
                "lapis_lazuli_card",
                "compass_card",
                "clock_card",
                "cookie_card",
                "ender_pearl_card",
                "ender_eye_card",
                "blaze_powder_card",
                "firework_rocket_card",
                "heart_of_the_sea_card",
                "nautilus_shell_card",
                "music_disc_card",
                "bell_card",
                "campfire_card",
                "lantern_card",
                "honeycomb_card",
                "amethyst_shard_card",
                "netherite_ingot_card",
                "feather_card",
                "spyglass_card"
        );
    }

    private record ItemRegistryHelper(RegistrationManager<Item> manager) {

        public <T extends Item> Reference<T> registerItem(String name, Function<Item.Properties, T> constructor) {
            return this.manager.register(name, identifier -> {
                ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, identifier);
                var props = new Item.Properties().setId(resourceKey);
                return constructor.apply(props);
            });
        }

        public String getNamespace() {
            return this.manager.getNamespace();
        }
    }
}
