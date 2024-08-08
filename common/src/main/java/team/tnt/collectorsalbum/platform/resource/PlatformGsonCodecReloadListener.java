package team.tnt.collectorsalbum.platform.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.message.FormattedMessage;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.List;
import java.util.Map;

public abstract class PlatformGsonCodecReloadListener<T> extends PlatformGsonReloadListener {

    public static final Gson GSON = new Gson();

    private final Codec<T> codec;

    public PlatformGsonCodecReloadListener(String dir, Codec<T> codec) {
        super(GSON, dir);
        this.codec = codec;
    }

    public Codec<T> codec() {
        return codec;
    }

    public List<T> getNetworkData() {
        return null;
    }

    public void onNetworkDataReceived(List<T> collection) {
    }

    protected abstract void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler);

    protected abstract void resolve(ResourceLocation path, T element);

    protected boolean filterEntry(ResourceLocation identifier) {
        return true;
    }

    @Override
    public final void apply(Map<ResourceLocation, JsonElement> resource, ResourceManager manager, ProfilerFiller profiler) {
        this.preApply(resource, manager, profiler);
        for (Map.Entry<ResourceLocation, JsonElement> entry : resource.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            if (!this.filterEntry(identifier))
                continue;
            JsonElement element = entry.getValue();
            try {
                DataResult<T> dataResult = this.codec.parse(JsonOps.INSTANCE, element);
                T data = this.readData(identifier, dataResult);
                this.resolve(identifier, data);
            } catch (Exception e) {
                this.handleParsingError(e, identifier);
            }
        }
        this.onReloadComplete(manager, profiler);
    }

    protected void onReloadComplete(ResourceManager manager, ProfilerFiller profiler) {
        List<T> data = this.getNetworkData();
        if (data != null) {
            PlatformNetworkManager.NETWORK.sendAllClientMessage(new S2C_SendDatapackResources<>(this));
        }
    }

    protected void handleParsingError(Exception e, ResourceLocation currentPath) {
        LOGGER.error(new FormattedMessage("Failed to parse {} element due to error", currentPath), e);
    }

    protected T readData(ResourceLocation path, DataResult<T> dataResult) {
        return dataResult.getOrThrow();
    }
}
