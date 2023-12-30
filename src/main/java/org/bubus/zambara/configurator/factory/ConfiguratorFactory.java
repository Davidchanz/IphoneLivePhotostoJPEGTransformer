package org.bubus.zambara.configurator.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public interface ConfiguratorFactory<T> {
    default Class<T> getConfiguratorType(){
        for (Type genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericInterface;
                return (Class<T>) (type).getActualTypeArguments()[0];
            }
        }
        throw new RuntimeException("Error get ConfiguratorFactory type [" + this +"]");
    };
    void initialize(Set<Class<T>> configurators);
}
