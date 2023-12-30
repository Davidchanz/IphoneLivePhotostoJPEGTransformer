package org.bubus.zambara.configurator;

import org.bubus.zambara.context.InternalContext;

public interface ContextConfigurator{
    default void config(InternalContext internalContext){};

}
