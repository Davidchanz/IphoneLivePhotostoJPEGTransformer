package org.bubus.spring.bean;

import java.util.*;
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

    public  <R> Collection<R> getBeansByInterface(Class<R> clazz) {
        Collection<R> beans = new HashSet<>();
        for (T item : this.values()) {
            for (Class<?> anInterface : item.getClazz().getInterfaces()) {
                Set<Class<?>> interfaces =
                        new HashSet<>(Arrays.stream(item.getClazz().getInterfaces()).toList());
                findSubInterfaces(interfaces, anInterface);
                for (Class<?> aClass : interfaces) {
                    if(aClass.equals(clazz)){
                        beans.add((R) item.getObject());
                    }
                }
            }
        }
        return beans;
    }

    public void findSubInterfaces(Set<Class<?>> container, Class<?> anInterface) {
        Class<?>[] interfaces = anInterface.getInterfaces();
        for (Class<?> aClass : interfaces) {
            findSubInterfaces(container, aClass);
            container.add(aClass);
        }
    }
}
