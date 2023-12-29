package org.bubus.spring.context;

import org.bubus.spring.bean.Bean;
import org.bubus.spring.bean.BeanDefinition;
import org.bubus.spring.bean.IoCContainer;
import org.bubus.spring.configurator.ContextConfigurationRegister;
import org.bubus.spring.configurator.factory.ConfiguratorFactory;

import java.util.Set;

public interface InternalContext extends Context {
    IoCContainer<BeanDefinition> getBeanDefinitionsContainer();
    IoCContainer<Bean> getBeansContainer();
    Set<ConfiguratorFactory> getConfiguratorFactories();
    ContextConfigurationRegister getContextListenerRegister();
    void putBean(Bean bean);
    void putBeanDefinition(BeanDefinition beanDefinition);
    void startContext();
    void refreshContext();
    void stopContext();
}
