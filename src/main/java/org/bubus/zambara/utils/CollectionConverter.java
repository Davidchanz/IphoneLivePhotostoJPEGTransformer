package org.bubus.zambara.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public interface CollectionConverter<T> {
    Collection<?> convert(Collection<?> collection);
    default Class<T> getConfiguratorType(){
        for (Type genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericInterface;
                return (Class<T>) (type).getActualTypeArguments()[0];
            }
        }
        throw new RuntimeException("Error get ContextListener type [" + this +"]");
    };
    default boolean isSupport(Class<T> fieldType){
        return getConfiguratorType().equals(fieldType);
    };
}
