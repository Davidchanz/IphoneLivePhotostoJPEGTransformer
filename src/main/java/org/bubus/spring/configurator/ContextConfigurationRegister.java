package org.bubus.spring.configurator;

import org.bubus.spring.configurator.factory.ConfiguratorFactory;
import org.bubus.spring.context.InternalContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContextConfigurationRegister {
    private InternalContext internalContext;
    private Map<Class<? extends ContextConfigurator>, Set<? extends ContextConfigurator>>
            configuratorsMap = new HashMap<>();
    public ContextConfigurationRegister(InternalContext internalContext){
        this.internalContext = internalContext;
    }

    public void registerContextConfigurators() {
        for (Class<? extends ContextConfigurator> configurator : findAllConfigurators()) {
            configuratorsMap.put(configurator, getConfigurations(configurator));
        }
    }

    public <T extends ContextConfigurator> Set<T> getConfigurators(Class<T> configurator){
        return this.configuratorsMap.get(configurator).stream().map(contextConfigurator -> (T) contextConfigurator).collect(Collectors.toSet());
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

    private <T> Function<Class<T>, T> getClassConfigurators(Method config) {
        return (aClass) -> {
            try {
                T object = aClass.getDeclaredConstructor().newInstance();
                config.invoke(object, internalContext);
                return object;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


    private <T extends ContextConfigurator> Set<T> getConfigurations(Class<T> anInterface){
        Set<T> objects = new HashSet<>();
        Set<Class<T>> inheritances = findInheritances(anInterface);
        inheritances.forEach(aClass -> {
            try {
                Method configMethod = aClass.getMethod("config", InternalContext.class);
                Function<Class<T>, T> classConfigurators = getClassConfigurators(configMethod);
                T object = classConfigurators.apply(aClass);
                objects.add(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        filterBeanDefinitionsContainer(anInterface);
        return objects;
    }

    private <T> Set<Class<T>> findInheritances(Class<T> target) {
        Set<Class<T>> inheritances = new HashSet<>();
        for (Class<?> clazz : this.internalContext.getBeanDefinitionsContainer().getBeanDefinitionClasses()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(target)){
                    inheritances.add((Class<T>)clazz);
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

    public<T extends ConfiguratorFactory> T getConfigurator(Class<T> configuratorFactoryClass) {
        Set<ConfiguratorFactory> configurators = getConfigurators(ConfiguratorFactory.class);
        for (ConfiguratorFactory configurator : configurators) {
            if(configurator.getClass().equals(configuratorFactoryClass)){
                return (T) configurator;
            }
        }
        return null;
    }
}
