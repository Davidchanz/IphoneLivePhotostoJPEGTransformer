package org.bubus.zambara.context;

import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanContainer;
import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.bean.BeanDefinitionContainer;
import org.bubus.zambara.bean.scope.Scope;
import org.bubus.zambara.configurator.factory.ScopeFactory;
import org.bubus.zambara.bpp.BeanPostProcessor;
import org.bubus.zambara.configurator.factory.BeanPostProcessorFactory;
import org.bubus.zambara.configurator.ContextConfigurationRegister;
import org.bubus.zambara.configurator.factory.ContextListenerFactory;
import org.bubus.zambara.context.state.ContextRefreshed;
import org.bubus.zambara.context.state.ContextStarted;
import org.bubus.zambara.context.state.ContextStopped;
import org.bubus.zambara.exception.BeanNotFoundException;
import org.bubus.zambara.listener.ContextListener;

import java.util.*;

public class ApplicationInternalContext implements InternalContext {
    private BeanDefinitionContainer beanDefinitionsContainer;
    private BeanContainer beanContainer = new BeanContainer();
    private ContextConfigurationRegister contextConfigurationRegister;
    private ContextListenerFactory contextListenerFactory;
    private BeanPostProcessorFactory beanPostProcessorFactory;
    private ScopeFactory scopeFactory;

    public ApplicationInternalContext(BeanDefinitionContainer beanDefinitionsContainer){
        this.beanDefinitionsContainer = beanDefinitionsContainer;
        this.startContext();
        this.refreshContext();
    }

    @Override
    public <T> T getBean(Class<T> clazz){
        BeanDefinition beanDefinition = this.beanDefinitionsContainer.get(clazz);
        if(beanDefinition == null)
            throw new BeanNotFoundException("No Bean [" + clazz + "]", clazz);

        Bean bean = this.scopeFactory.getBean(beanDefinition);
        //Bean bean = this.beanContainer.get(clazz);

        if(bean == null){
            Collection<Class<?>> beansByInterface = this.beanContainer.getBeansByInterface(clazz);
            if(beansByInterface.isEmpty())
                throw new BeanNotFoundException("No Bean [" + clazz + "]", clazz);
            if(beansByInterface.size() > 1)
                throw new RuntimeException("There are several Beans [" + clazz + "], founded Beans{" + beansByInterface + "}");
            return (T) getBean(beansByInterface.iterator().next());
        }else
            return (T) bean.getObject();
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        Collection<T> beans = new HashSet<>();
        Collection<BeanDefinition> beanDefinitionsByInterface = this.beanDefinitionsContainer.getBeanDefinitionsByInterface(clazz);
        if(beanDefinitionsByInterface.isEmpty())
            throw new BeanNotFoundException("Bean with id [" + this.beanContainer.getBeanKey(clazz) + "] not exist!", clazz);
        for (BeanDefinition t : beanDefinitionsByInterface) {
            beans.add((T) getBean(t.getClazz()));
        }
        return beans;
    }

    @Override
    public void putBean(Bean bean){
        this.beanContainer.put(bean);
    }

    @Override
    public void putBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitionsContainer.put(beanDefinition);
    }


    @Override
    public BeanDefinitionContainer getBeanDefinitionsContainer() {
        return this.beanDefinitionsContainer;
    }

    @Override
    public BeanContainer getBeansContainer() {
        return this.beanContainer;
    }

    @Override
    public ContextConfigurationRegister getContextConfigurationRegister() {
        return contextConfigurationRegister;
    }

    @Override
    public void startContext() {
        this.contextConfigurationRegister = new ContextConfigurationRegister(this);
        this.contextConfigurationRegister.registerContextConfigurators();

        //TODO make all of this internal

        //Scope
        this.scopeFactory = new ScopeFactory(this);
        Set<Class<Scope>> scopes = contextConfigurationRegister
                .getConfigurators(Scope.class);
        this.scopeFactory.initialize(scopes);

        //BeanPostProcessors
        this.beanPostProcessorFactory = new BeanPostProcessorFactory(this);
        Set<Class<BeanPostProcessor>> beanPostProcessors = contextConfigurationRegister
                .getConfigurators(BeanPostProcessor.class);
        this.beanPostProcessorFactory.initialize(beanPostProcessors);

        //ContextListeners
        this.contextListenerFactory = new ContextListenerFactory(this);
        Set<Class<ContextListener>> contextListeners = this.contextConfigurationRegister
                .getConfigurators(ContextListener.class);
        this.contextListenerFactory.initialize(contextListeners);

        this.contextListenerFactory.listen(ContextStarted.class);
    }

    @Override
    public void refreshContext() {
        this.contextListenerFactory.listen(ContextRefreshed.class);
    }

    @Override
    public void stopContext() {
        this.contextListenerFactory.listen(ContextStopped.class);
    }

    @Override
    public void close() throws Exception {
        this.stopContext();
    }
}
