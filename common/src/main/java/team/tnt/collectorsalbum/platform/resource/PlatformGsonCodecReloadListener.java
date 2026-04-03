package team.tnt.collectorsalbum.platform.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.message.FormattedMessage;

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

    protected abstract void preApply(Map<Identifier, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler);

    protected abstract void resolve(Identifier path, T element);

    protected boolean filterEntry(Identifier identifier) {
        return true;
    }

    @Override
    public final void apply(Map<Identifier, JsonElement> resource, ResourceManager manager, ProfilerFiller profiler) {
        this.preApply(resource, manager, profiler);
        for (Map.Entry<Identifier, JsonElement> entry : resource.entrySet()) {
            Identifier identifier = entry.getKey();
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
    }

    protected void handleParsingError(Exception e, Identifier currentPath) {
        LOGGER.error(new FormattedMessage("Failed to parse {} element due to error", currentPath), e);
    }

    protected T readData(Identifier path, DataResult<T> dataResult) {
        return dataResult.getOrThrow();
    }
}
