package team.tnt.collectorsalbum.platform.resource;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.platform.Identifiable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class PlatformBaseReloadListener<T> implements PreparableReloadListener, Identifiable {

    public static final Logger LOGGER = LogManager.getLogger("PlatformBaseReloadListener");

    @Override
    public final CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationProfiler, ProfilerFiller applyProfiler, Executor preparationExec, Executor applyExec) {
        return CompletableFuture.supplyAsync(() -> prepare(manager, preparationProfiler), preparationExec)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(resource -> apply(resource, manager, applyProfiler), applyExec);
    }

    @Override
    public final String getName() {
        return this.identifier().toString();
    }

    public abstract T prepare(ResourceManager manager, ProfilerFiller profiler);

    public abstract void apply(T resource, ResourceManager manager, ProfilerFiller profiler);
}
