package org.bubus.spring.context;

import org.bubus.spring.bean.Bean;
import org.bubus.spring.bean.BeanDefinition;
import org.bubus.spring.bean.IoCContainer;
import org.bubus.spring.configurator.factory.ConfiguratorFactory;
import org.bubus.spring.configurator.ContextConfigurationRegister;
import org.bubus.spring.context.state.ContextRefreshed;
import org.bubus.spring.context.state.ContextStarted;
import org.bubus.spring.context.state.ContextStopped;
import org.bubus.spring.listener.ContextListener;
import org.bubus.spring.listener.ContextListenerStarter;

import java.util.*;

public class ApplicationInternalContext implements InternalContext {
    private ContextConfigurationRegister contextListenerRegister;
    private Set<ConfiguratorFactory> configuratorFactories;
    private IoCContainer<BeanDefinition> beanDefinitionsContainer;
    private ContextListenerStarter contextListenerStarter;
    private IoCContainer<Bean> beanIoCContainer = new IoCContainer<>();
    public ApplicationInternalContext(IoCContainer<BeanDefinition> beanDefinitionsContainer){
        this.beanDefinitionsContainer = beanDefinitionsContainer;
        this.startContext();
        this.refreshContext();
    }

    @Override
    public <T> T getBean(Class<T> clazz){
        Bean bean = this.beanIoCContainer.get(clazz);
        return (T) bean.getObject();
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        Collection<T> beans = new HashSet<>();
        for (Bean bean : this.beanIoCContainer.values()) {
            for (Class<?> anInterface : bean.getClazz().getInterfaces()) {
                Set<Class<?>> interfaces = new HashSet<>();
                interfaces.addAll(Arrays.stream(bean.getClazz().getInterfaces()).toList());
                findSubInterfaces(interfaces, anInterface, clazz);
                for (Class<?> aClass : interfaces) {
                    if(aClass.equals(clazz)){
                        beans.add((T) bean.getObject());
                    }
                }
            }
        }
        if(beans.isEmpty())
            new RuntimeException("Bean with id [" + this.beanIoCContainer.getBeanKey(clazz) + "] not exist!");
        return beans;
    }

    @Override
    public void addBean(Bean bean){
        this.beanIoCContainer.put(bean);
    }

    @Override
    public IoCContainer<BeanDefinition> getBeanDefinitionsContainer() {
        return this.beanDefinitionsContainer;
    }

    @Override
    public IoCContainer<Bean> getBeansContainer() {
        return this.beanIoCContainer;
    }

    @Override
    public Set<ConfiguratorFactory> getConfiguratorFactories() {
        return configuratorFactories;
    }

    @Override
    public ContextConfigurationRegister getContextListenerRegister() {
        return contextListenerRegister;
    }

    @Override
    public void startContext() {
        this.contextListenerRegister = new ContextConfigurationRegister(this);
        this.contextListenerRegister.registerContextConfigurators();
        this.configuratorFactories = this.contextListenerRegister.getConfigurators(ConfiguratorFactory.class);

        Set<ContextListener> contextListeners = this.contextListenerRegister.getConfigurators(ContextListener.class);
        this.contextListenerStarter = new ContextListenerStarter(contextListeners);

        this.contextListenerStarter.listen(ContextStarted.class);
    }

    @Override
    public void refreshContext() {
        this.contextListenerStarter.listen(ContextRefreshed.class);
    }

    @Override
    public void stopContext() {
        this.contextListenerStarter.listen(ContextStopped.class);
    }

    private void findSubInterfaces(Set<Class<?>> container, Class<?> anInterface, Class<?> targetInterface) {
        Class<?>[] interfaces = anInterface.getInterfaces();
        for (Class<?> aClass : interfaces) {
            findSubInterfaces(container, aClass, targetInterface);
            container.add(aClass);
        }
    }

    @Override
    public void close() throws Exception {
        this.stopContext();
    }
}
