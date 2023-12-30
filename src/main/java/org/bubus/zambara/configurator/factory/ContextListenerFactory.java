package org.bubus.zambara.configurator.factory;

import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.context.state.ContextState;
import org.bubus.zambara.listener.ContextListener;

import java.util.HashSet;
import java.util.Set;

public class ContextListenerFactory extends AbstractConfiguratorFactory<ContextListener>{

    private Set<ContextListener> contextListeners = new HashSet<>();

    public ContextListenerFactory(InternalContext internalContext) {
        super(internalContext);
    }

    @Override
    public void initialize(Set<Class<ContextListener>> contextListenersClazzs) {
        for (Class<ContextListener> contextListenersClazz : contextListenersClazzs) {
            ContextListener contextListener = preConstructContextConfigure(contextListenersClazz);
            this.contextListeners.add(contextListener);
        }
    }

    public void listen(Class<? extends ContextState> contextEventType){
        for (ContextListener contextListener : this.contextListeners) {
            if(contextListener.support(contextEventType)){
                contextListener.listen();
            }
        }
    }
}
