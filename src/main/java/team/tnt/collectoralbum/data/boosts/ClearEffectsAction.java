package team.tnt.collectoralbum.data.boosts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import team.tnt.collectoralbum.common.init.ActionTypeRegistry;
import team.tnt.collectoralbum.util.JsonHelper;

public class ClearEffectsAction implements IAction {

    private final MobEffect[] effects;

    public ClearEffectsAction(MobEffect[] effects) {
        this.effects = effects;
    }

    @Override
    public ActionType<?> getType() {
        return ActionTypeRegistry.CLEAR_EFFECTS;
    }

    @Override
    public void apply(IBoostContext context) {
        Player player = context.get(SimpleBoostContext.PLAYER, Player.class);
        for (MobEffect effect : effects) {
            player.removeEffect(effect);
        }
    }

    @Override
    public Component[] getDescription() {
        return new Component[0];
    }

    public static final class Serializer implements IActionSerializer<ClearEffectsAction> {

        @Override
        public ClearEffectsAction fromJson(JsonObject data, OpType opType) throws JsonParseException {
            JsonArray array = GsonHelper.getAsJsonArray(data, "effects");
            MobEffect[] effects = JsonHelper.resolveRegistryObjectsFromIdArray(array, BuiltInRegistries.MOB_EFFECT, MobEffect[]::new);
            return new ClearEffectsAction(effects);
        }

        @Override
        public void networkEncode(ClearEffectsAction action, FriendlyByteBuf buffer) {
            buffer.writeInt(action.effects.length);
            for (MobEffect effect : action.effects) {
                buffer.writeResourceLocation(ForgeRegistries.MOB_EFFECTS.getKey(effect));
            }
        }

        @Override
        public ClearEffectsAction networkDecode(ActionType<ClearEffectsAction> type, FriendlyByteBuf buffer) {
            int count = buffer.readInt();
            MobEffect[] effects = new MobEffect[count];
            for (int i = 0; i < count; i++) {
                effects[i] = ForgeRegistries.MOB_EFFECTS.getValue(buffer.readResourceLocation());
            }
            return new ClearEffectsAction(effects);
        }
    }
}
