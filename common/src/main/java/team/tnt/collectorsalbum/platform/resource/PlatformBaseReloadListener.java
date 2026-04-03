package team.tnt.collectorsalbum.platform.resource;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.platform.Identifiable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class PlatformBaseReloadListener<T> implements PreparableReloadListener, Identifiable {

    public static final Logger LOGGER = LogManager.getLogger("PlatformBaseReloadListener");

    @Override
    public final CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor) {
        ResourceManager manager = currentReload.resourceManager();
        return CompletableFuture.supplyAsync(() -> prepare(manager, Profiler.get()), taskExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(resource -> apply(resource, manager, Profiler.get()), reloadExecutor);
    }

    @Override
    public final String getName() {
        return this.identifier().toString();
    }

    public abstract T prepare(ResourceManager manager, ProfilerFiller profiler);

    public abstract void apply(T resource, ResourceManager manager, ProfilerFiller profiler);
}
