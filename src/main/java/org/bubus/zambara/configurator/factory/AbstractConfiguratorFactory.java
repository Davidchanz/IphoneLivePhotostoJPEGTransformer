package org.bubus.zambara.configurator.factory;

import org.bubus.zambara.context.InternalContext;

import java.lang.reflect.Method;
import java.util.function.Function;

public abstract class AbstractConfiguratorFactory<T> implements ConfiguratorFactory<T>{
    protected InternalContext internalContext;

    public AbstractConfiguratorFactory(InternalContext internalContext){
        this.internalContext = internalContext;
    }

    public <R extends T> R preConstructContextConfigure(Class<R> contextConfigureClazz) {
        T beanPostProcessor = constructBeanPostProcessor(contextConfigureClazz);
        return (R) beanPostProcessor;
    }

    private <R extends T> T constructBeanPostProcessor(Class<R> contextConfigureClazz) {
        try {
            Method configMethod = contextConfigureClazz.getMethod("config", InternalContext.class);
            Function<Class<R>, R> classConfigurator = getClassConfigurator(configMethod);
            return classConfigurator.apply(contextConfigureClazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Function<Class<T>, T> getClassConfigurator(Method config) {
        return (aClass) -> {
            try {
                T object = aClass.getDeclaredConstructor().newInstance();
                config.invoke(object, this.internalContext);
                return object;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
