package org.bubus.zambara.bean.scope;

import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.exception.BeanNotFoundException;

public class PrototypeScope implements Scope {
    private InternalContext internalContext;
    @Override
    public void config(InternalContext internalContext) {//TODO internal abstract class for all ContextConfigurators (BPP, ContextListeners)
        this.internalContext = internalContext;
    }
    @Override
    public Bean getObject(Class<?> clazz) {
        Bean bean = this.internalContext.getBeansContainer().get(clazz);
        if(bean != null) {
            String beanKey = this.internalContext.getBeansContainer().getBeanKey(bean.getClazz());
            this.internalContext.getBeansContainer().remove(beanKey);
        }
        return bean;
    }

    @Override
    public boolean isSupport(String scope) {
        return scope.equalsIgnoreCase("prototype");
    }
}
