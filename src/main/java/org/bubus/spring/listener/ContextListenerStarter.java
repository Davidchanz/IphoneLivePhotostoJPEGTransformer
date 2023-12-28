package org.bubus.spring.listener;

import org.bubus.spring.context.state.ContextRefreshed;
import org.bubus.spring.context.state.ContextState;

import java.util.Set;

public class ContextListenerStarter {
    private Set<ContextListener> contextListeners;
    public ContextListenerStarter(Set<ContextListener> contextListeners){
        this.contextListeners = contextListeners;
    }
    public void listen(Class<? extends ContextState> contextEventType){
        for (ContextListener contextListener : this.contextListeners) {
            if(contextListener.support(contextEventType)){
                contextListener.listen();
            }
        }
    }
}
