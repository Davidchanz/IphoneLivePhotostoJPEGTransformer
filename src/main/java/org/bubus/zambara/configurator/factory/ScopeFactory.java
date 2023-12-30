package org.bubus.zambara.configurator.factory;

import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.bean.scope.Scope;
import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.listener.ContextListener;

import java.util.HashSet;
import java.util.Set;

public class ScopeFactory extends AbstractConfiguratorFactory<Scope> {
    private Set<Scope> scopes = new HashSet<>();
    public ScopeFactory(InternalContext internalContext){
        super(internalContext);
    }

    @Override
    public void initialize(Set<Class<Scope>> configurators) {
        for (Class<Scope> contextListenersClazz : configurators) {
            Scope scope = preConstructContextConfigure(contextListenersClazz);
            this.scopes.add(scope);
        }
    }

    public Bean getBean(BeanDefinition beanDefinition) {
        for (Scope scope : this.scopes) {
            if(scope.isSupport(beanDefinition.getScope())) {
                Bean bean = scope.getObject(beanDefinition.getClazz());
                return bean;
            }
        }
        return null;
    }
}
