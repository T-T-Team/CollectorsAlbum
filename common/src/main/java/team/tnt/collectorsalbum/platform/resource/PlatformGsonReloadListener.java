package team.tnt.collectorsalbum.platform.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.message.FormattedMessage;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class PlatformGsonReloadListener extends PlatformBaseReloadListener<Map<ResourceLocation, JsonElement>> {

    protected final Gson gson;
    protected final String directory;

    public PlatformGsonReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    public final Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> resources = new HashMap<>();
        FileToIdConverter idConverter = FileToIdConverter.json(this.directory);
        for (Map.Entry<ResourceLocation, Resource> entry : idConverter.listMatchingResources(manager).entrySet()) {
            ResourceLocation fileResourcePath = entry.getKey();
            ResourceLocation identifier = idConverter.fileToId(fileResourcePath);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(this.gson, reader, JsonElement.class);
                if (resources.put(identifier, json) != null) {
                    throw new IllegalStateException("Duplicate data file: " + identifier);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException e) {
                LOGGER.error(new FormattedMessage("Could not parse data file {} from {}", identifier, fileResourcePath), e);
            }
        }
        return resources;
    }
}
