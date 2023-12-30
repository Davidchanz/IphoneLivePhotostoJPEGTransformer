package org.bubus.zambara.bean.scope;

import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.context.InternalContext;

@Component
public class SingletonScope implements Scope{
    private InternalContext internalContext;
    @Override
    public void config(InternalContext internalContext) {//TODO internal abstract class for all ContextConfigurators (BPP, ContextListeners)
        this.internalContext = internalContext;
    }

    @Override
    public Bean getObject(Class<?> clazz) {
        Bean bean = this.internalContext.getBeansContainer().get(clazz);
        return bean;
    }

    @Override
    public boolean isSupport(String scope) {
        return scope.equalsIgnoreCase("singleton");
    }
}
