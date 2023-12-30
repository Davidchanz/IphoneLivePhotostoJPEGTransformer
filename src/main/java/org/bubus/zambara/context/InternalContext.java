package org.bubus.zambara.context;

import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanContainer;
import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.bean.BeanDefinitionContainer;
import org.bubus.zambara.configurator.ContextConfigurationRegister;

public interface InternalContext extends Context {
    BeanDefinitionContainer getBeanDefinitionsContainer();
    BeanContainer getBeansContainer();
    ContextConfigurationRegister getContextConfigurationRegister();
    void putBean(Bean bean);
    void putBeanDefinition(BeanDefinition beanDefinition);
    void startContext();
    void refreshContext();
    void stopContext();
}
