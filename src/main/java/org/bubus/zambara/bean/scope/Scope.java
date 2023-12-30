package org.bubus.zambara.bean.scope;

import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.configurator.ContextConfigurator;

@Component
public interface Scope extends ContextConfigurator {
    Bean getObject(Class<?> clazz);
    boolean isSupport(String scope);
}
