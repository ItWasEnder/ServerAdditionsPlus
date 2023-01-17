package me.endergaming.plugins.backend.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark methods as being event handler methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    /**
     * Whether the event is called async by bukkit.
     *
     * @return true if the event is called async
     */
    boolean async() default false;

    /**
     * The priority of the event handler method. The lower the value the sooner your method will be called.
     * Default value is <code>1000</code>
     *
     * @return The priority of the event handler method.
     */
    int priority() default 1000;

    boolean ignoreCancelled() default true;
}
