package top.spco.core;

import top.spco.service.RegistrationException;
import top.spco.service.command.exceptions.CommandRegistrationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示某个类型的管理器。
 *
 * @param <V> 要管理的类型
 * @version 4.0.0
 * @since 4.0.0
 * @author SpCo
 */
public abstract class Manager<K,V> {
    private final Map<K, V> registeredObject = new HashMap<>();

    protected Manager() {

    }

    public void register(K value, V object) throws RegistrationException {
        if (registeredObject.containsKey(value)) {
            throw new CommandRegistrationException(value + " is registered in the " + this.getClass().getName() + ".");
        }
        registeredObject.put(value, object);
    }

    public V get(K key) {
        return registeredObject.get(key);
    }

    public Map<K, V> getAllRegistered() {
        return registeredObject;
    }
}
