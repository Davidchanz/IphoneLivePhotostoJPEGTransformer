package org.bubus.context;

import org.bubus.context.annotation.Component;

@Component
public interface BeanPostProcessor {
    Object construct(Class<?> clazz);
}
