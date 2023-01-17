package me.endergaming.plugins.backend.events;

import com.marcusslover.plus.lib.util.ReadWriteLock;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
public class ObserverList {
    private final ReadWriteLock lock = new ReadWriteLock();

    private boolean isAsync = false;

    final List<WrappedListener> observers = new LinkedList<>();

    public void add(WrappedListener listener) {
        if (this.isAsync) {
            try {
                this.lock.writeLock();
                this.observers.add(listener);
            } finally {
                this.lock.writeUnlock();
            }
        } else {
            this.observers.add(listener);
        }
    }

    public void remove(WrappedListener listener) {
        this.observers.remove(listener);
    }

    public ObserverList async() {
        this.isAsync = true;
        return this;
    }

    void forEach(Consumer<WrappedListener> action) {
        Objects.requireNonNull(action);
        for (var t : this.observers) {
            action.accept(t);
        }
    }

    public ObserverList sort() {
        if (this.isAsync) {
            /* Read Operations */
            this.lock.readLock();
            var sort = new java.util.ArrayList<>(this.observers);
            this.lock.readUnlock();

            /* Write Operations */
            this.lock.writeLock();
            this.observers.clear();
            sort.sort(Comparator.comparingInt(WrappedListener::getPriority));
            this.observers.addAll(sort);
            this.lock.writeUnlock();
        } else {
            this.observers.sort(Comparator.comparingInt(WrappedListener::getPriority));
        }

        return this;
    }
}
