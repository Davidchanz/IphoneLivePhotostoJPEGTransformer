package org.bubus.zambara.bpp;

import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.configurator.ContextConfigurator;

@Component
public interface BeanPostProcessor extends ContextConfigurator {
    BeanDefinition preConstruct(BeanDefinition beanDefinition) throws Exception;
    Bean postConstruct(Bean bean) throws Exception;
}
