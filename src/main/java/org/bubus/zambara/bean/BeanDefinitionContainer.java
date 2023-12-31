package org.bubus.zambara.bean;

import java.util.*;
import java.util.stream.Collectors;

public class BeanDefinitionContainer extends HashMap<String, BeanDefinition> {
    public BeanDefinitionContainer(){
        super();
    }
    public BeanDefinition get(Class<?> clazz) {
        return super.get(getBeanKey(clazz));
    }

    public Set<Class<?>> getBeanDefinitionClasses(){
        return super.values().stream().map(BeanDefinition::getClazz).collect(Collectors.toSet());
    }

    public BeanDefinition put(BeanDefinition beanDefinition) {
        return super.put(getBeanKey(beanDefinition.getClazz()), beanDefinition);
    }

    public String getBeanKey(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    public Collection<BeanDefinition> getBeanDefinitionsByInterface(Class<?> targetInterfaces) {
        Collection<BeanDefinition> beans = new HashSet<>();
        for (BeanDefinition item : this.values()) {
            Set<Class<?>> superClasses = new HashSet<>(Collections.singleton(item.getClazz()));
            findAllSuperClass(superClasses, item.getClazz());
            for (Class<?> superClass : superClasses) {
                for (Class<?> anInterface : superClass.getInterfaces()) {
                    Set<Class<?>> interfaces =
                            new HashSet<>(Arrays.stream(superClass.getInterfaces()).toList());
                    findSubInterfaces(interfaces, anInterface);
                    for (Class<?> anInterfaces : interfaces) {
                        if(anInterfaces.equals(targetInterfaces)){
                            beans.add(item);
                        }
                    }
                }
            }
        }
        return beans;
    }

    private void findAllSuperClass(Set<Class<?>> container, Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) {
            findAllSuperClass(container, superClass);
            container.add(superClass);
        }
    }

    public void findSubInterfaces(Set<Class<?>> container, Class<?> anInterface) {
        Class<?>[] interfaces = anInterface.getInterfaces();
        for (Class<?> aClass : interfaces) {
            findSubInterfaces(container, aClass);
            container.add(aClass);
        }
    }
}
