package org.bubus.spring.bean;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class IoCContainer<T extends IoCContainerItem> extends HashMap<String, T> {
    public IoCContainer(){
        super();
    }
    public T get(Class<?> clazz) {
        return super.get(getBeanKey(clazz));
    }

    public Set<Class<?>> getBeanDefinitionClasses(){
        return super.values().stream().map(T::getClazz).collect(Collectors.toSet());
    }

    public T put(T object) {
        return super.put(getBeanKey(object.getClazz()), object);
    }

    public String getBeanKey(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
