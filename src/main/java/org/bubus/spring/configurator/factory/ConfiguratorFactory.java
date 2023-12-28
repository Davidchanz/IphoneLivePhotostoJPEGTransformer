package org.bubus.spring.configurator.factory;

import org.bubus.spring.annotation.Component;
import org.bubus.spring.configurator.ContextConfigurator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

@Component
public interface ConfiguratorFactory<T> extends ContextConfigurator {
    default Class<T> getConfiguratorType(){
        for (Type genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericInterface;
                return (Class<T>) (type).getActualTypeArguments()[0];
            }
        }
        throw new RuntimeException("Error get ConfiguratorFactory type [" + this +"]");
    };
    void process(Set<T> configurators);
}
