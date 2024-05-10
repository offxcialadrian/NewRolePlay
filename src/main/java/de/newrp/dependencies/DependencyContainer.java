package de.newrp.dependencies;

import de.newrp.API.Debug;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyContainer {

    public static DependencyContainer INSTANCE = new DependencyContainer();
    private final Map<Class<?>, Object> registeredDependencies;

    public DependencyContainer() {
        this.registeredDependencies = new ConcurrentHashMap<>();
    }

    public void add(final Class<?> type, final Object instance) {
        this.registeredDependencies.put(type, instance);
    }

    public <T> T getDependency(final Class<? extends T> type) {
        if(!registeredDependencies.containsKey(type)) {
            Debug.debug("Dependency Container doesn't know type " + type.getName());
            return null;
        }

        return (T) registeredDependencies.get(type);
    }

    public Map<Class<?>, Object> getRegisteredDependencies() {
        return registeredDependencies;
    }

    public static DependencyContainer getContainer() {
        return INSTANCE;
    }

}
