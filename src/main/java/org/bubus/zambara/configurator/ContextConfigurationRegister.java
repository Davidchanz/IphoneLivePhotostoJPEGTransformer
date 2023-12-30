package org.bubus.zambara.configurator;

import org.bubus.zambara.context.InternalContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextConfigurationRegister {
    private InternalContext internalContext;
    private Map<Class<? extends ContextConfigurator>, Set<Class<? extends ContextConfigurator>>>
            configuratorsMap = new HashMap<>();
    public ContextConfigurationRegister(InternalContext internalContext){
        this.internalContext = internalContext;
    }

    public void registerContextConfigurators() {
        for (Class<? extends ContextConfigurator> configurator : findAllConfigurators()) {
            this.configure(configurator);
        }
    }

    private void configure(Class<? extends ContextConfigurator> configurator) {
        configuratorsMap.put(configurator, getConfigurations(configurator));
    }

    public <T extends ContextConfigurator> Set<Class<T>> getConfigurators(Class<T> configurator){
        return this.configuratorsMap.get(configurator).stream().map(contextConfigurator -> (Class<T>) contextConfigurator).collect(Collectors.toSet());
    }

    private Set<Class<? extends ContextConfigurator>> findAllConfigurators() {
        Set<Class<? extends ContextConfigurator>> inheritances = new HashSet<>();
        for (Class<?> clazz : this.internalContext.getBeanDefinitionsContainer().getBeanDefinitionClasses()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(ContextConfigurator.class)){
                    inheritances.add((Class<? extends ContextConfigurator>) clazz);
                }
            }
        }
        return inheritances;
    }


    private Set<Class<? extends ContextConfigurator>> getConfigurations(Class<? extends ContextConfigurator> anInterface){
        Set<Class<? extends ContextConfigurator>> inheritances = findInheritances(anInterface);
        filterBeanDefinitionsContainer(anInterface);
        return inheritances;
    }

    private Set<Class<? extends ContextConfigurator>> findInheritances(Class<? extends ContextConfigurator> target) {
        Set<Class<? extends ContextConfigurator>> inheritances = new HashSet<>();
        for (Class<?> clazz : this.internalContext.getBeanDefinitionsContainer().getBeanDefinitionClasses()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(target)){
                    inheritances.add((Class<? extends ContextConfigurator>) clazz);
                }
            }
        }
        return inheritances;
    }

    private void filterBeanDefinitionsContainer(Class<?> clazz){
        Set<String> keys = new HashSet<>();
        keys.add(this.internalContext.getBeanDefinitionsContainer().getBeanKey(clazz));
        this.internalContext.getBeanDefinitionsContainer().forEach((s, aClass) -> {
            for (Class<?> anInterface : aClass.getClazz().getInterfaces()) {
                if (anInterface.equals(clazz)) {
                    keys.add(s);
                    break;
                }
            }
        });
        for (String key : keys) {
            this.internalContext.getBeanDefinitionsContainer().remove(key);
        }
    }
}
