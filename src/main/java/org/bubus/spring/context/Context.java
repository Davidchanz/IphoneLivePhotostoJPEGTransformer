package org.bubus.spring.context;

import java.util.Collection;

public interface Context extends AutoCloseable{
    <T> T getBean(Class<T> clazz);
    <T> Collection<T> getBeans(Class<T> clazz);
}
