package me.endergaming.plugins.backend.events;

import com.destroystokyo.paper.util.SneakyThrow;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.endergaming.plugins.ServerAdditionsPlus;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.AuthorNagException;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventManager implements Listener {
    private static EventManager instance;

    private final Map<Class<? extends org.bukkit.event.Event>, ObserverList> observers = new ConcurrentHashMap<>();

    public EventManager() {
        instance = this;
    }

    public void register(@NotNull EventListener observer) {
        Set<Method> methods;
        try {
            Method[] publicMethods = observer.getClass().getMethods();
            Method[] privateMethods = observer.getClass().getDeclaredMethods();

            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);

            methods.addAll(Arrays.asList(publicMethods));
            methods.addAll(Arrays.asList(privateMethods));
        } catch (NoClassDefFoundError e) {
            ServerAdditionsPlus.logger().severe("Failed to register events for " + observer.getClass() + " because " + e.getMessage() + " does not exist.");
            return;
        }

        for (final Method method : methods) {
            final Event eh = method.getAnnotation(Event.class);
            if (eh == null) {
                continue;
            }
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !org.bukkit.event.Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                ServerAdditionsPlus.logger().severe("attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + observer.getClass());
                continue;
            }
            final Class<? extends org.bukkit.event.Event> eventClass = checkClass.asSubclass(org.bukkit.event.Event.class);
            method.setAccessible(true);
            for (Class<?> clazz = eventClass; org.bukkit.event.Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Warning warning = clazz.getAnnotation(Warning.class);
                    Warning.WarningState warningState = Bukkit.getServer().getWarningState();
                    if (!warningState.printFor(warning)) {
                        break;
                    }
                    ServerAdditionsPlus.logger().log(
                            Level.WARNING,
                            String.format(
                                    "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.",
                                    ServerAdditionsPlus.getPlugin().getDescription().getFullName(),
                                    clazz.getName(),
                                    method.toGenericString(),
                                    (warning != null && warning.reason().length() != 0) ? warning.reason() : "Server performance will be affected",
                                    Arrays.toString(ServerAdditionsPlus.getPlugin().getDescription().getAuthors().toArray())),
                            warningState == Warning.WarningState.ON ? new AuthorNagException(null) : null);
                    break;
                }
            }

            try {
                this.getObservers(eventClass, eh.async())
                        .add(WrappedListener.of(observer, MethodHandles.lookup().unreflect(method), eh.priority(), eh.ignoreCancelled()));
            } catch (IllegalAccessException e) {
                SneakyThrow.sneaky(e);
            }
        }
    }

    /**
     * This will notify all observers of the event as well as create an observer mapping if one is not already cached.
     *
     * @param event The event to notify observers of.
     * @param <T>   The type of event.
     */
    public <T extends org.bukkit.event.Event> void notify(T event) {
        this.getObservers(event)
                .sort()
                .forEach(wrapped -> {
                    try {
                        var handler = wrapped.getMethodHandle();
                        var listener = wrapped.getListener();
                        var ignoreCancelled = wrapped.isIgnoreCancelled();

                        if (event instanceof Cancellable cancellable) {
                            if (cancellable.isCancelled() && !ignoreCancelled) {
                                return;
                            }
                        }

                        handler.invoke(listener, event);
                    } catch (Throwable e) {
                        SneakyThrow.sneaky(e);
                    }
                });
    }

    /**
     * Obtain all observers for the given event.
     *
     * @param event The event to obtain observers for.
     * @return The observer list.
     */
    public ObserverList getObservers(org.bukkit.event.Event event) {
        return this.getObservers(event.getClass(), event.isAsynchronous());
    }

    private ObserverList getObservers(Class<? extends org.bukkit.event.Event> eventClass, boolean async) {
        return this.observers.computeIfAbsent(eventClass, k -> new ObserverList(async));
    }

    /* Bukkit Events Start */

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onChatEvent(AsyncChatEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onProjectilLaunch(ProjectileLaunchEvent event) {
        this.notify(event);
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        this.notify(event);
    }

    /* Bukkit Events Stop */

    public static EventManager get() {
        return instance == null ? instance = new EventManager() : instance;
    }
}
