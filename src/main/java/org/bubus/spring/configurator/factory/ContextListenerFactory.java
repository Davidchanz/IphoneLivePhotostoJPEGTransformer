package org.bubus.spring.configurator.factory;

import org.bubus.spring.context.InternalContext;
import org.bubus.spring.listener.ContextListener;

import java.util.Set;

public class ContextListenerFactory implements ConfiguratorFactory<ContextListener>{
    private InternalContext internalContext;

    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void process(Set<ContextListener> contextListeners) {
        for (ContextListener contextListener : contextListeners) {
            contextListener.listen();
        }
    }
}
