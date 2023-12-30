package org.bubus.zambara.bean;

import java.util.*;
import java.util.stream.Collectors;

public class BeanContainer extends HashMap<String, Bean> {
    public BeanContainer(){
        super();
    }
    public Bean get(Class<?> clazz) {
        return super.get(getBeanKey(clazz));
    }

    public Bean put(Bean bean) {
        return super.put(getBeanKey(bean.getClazz()), bean);
    }

    public String getBeanKey(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    public Collection<Class<?>> getBeansByInterface(Class<?> targetInterfaces) {
        Collection<Class<?>> beanClasses = new HashSet<>();
        for (Bean item : this.values()) {
            for (Class<?> anInterface : item.getClazz().getInterfaces()) {
                Set<Class<?>> interfaces =
                        new HashSet<>(Arrays.stream(item.getClazz().getInterfaces()).toList());
                findSubInterfaces(interfaces, anInterface);
                for (Class<?> anInterfaces : interfaces) {
                    if(anInterfaces.equals(targetInterfaces)){
                        beanClasses.add(anInterfaces);
                    }
                }
            }
        }
        return beanClasses;
    }

    public void findSubInterfaces(Set<Class<?>> container, Class<?> anInterface) {
        Class<?>[] interfaces = anInterface.getInterfaces();
        for (Class<?> aClass : interfaces) {
            findSubInterfaces(container, aClass);
            container.add(aClass);
        }
    }
}
