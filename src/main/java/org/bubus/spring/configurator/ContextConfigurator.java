package org.bubus.spring.configurator;

import org.bubus.spring.context.InternalContext;

public interface ContextConfigurator{
    default void config(InternalContext internalContext){};

}
