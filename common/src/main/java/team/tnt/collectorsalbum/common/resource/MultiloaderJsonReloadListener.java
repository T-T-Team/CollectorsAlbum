package team.tnt.collectorsalbum.common.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public abstract class MultiloaderJsonReloadListener extends SimpleJsonResourceReloadListener {

    private final Gson gson;
    private final String dir;

    public MultiloaderJsonReloadListener(Gson gson, String dir) {
        super(gson, dir);
        this.gson = gson;
        this.dir = dir;
    }

    public abstract void loadData(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler);

    @Override
    protected final void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.loadData(resources, manager, profiler);
    }

    public Gson getGson() {
        return gson;
    }

    public String getDir() {
        return dir;
    }
}
