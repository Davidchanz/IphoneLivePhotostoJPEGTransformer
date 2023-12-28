package org.bubus.spring.bpp;

import org.bubus.spring.annotation.Component;
import org.bubus.spring.bean.Bean;
import org.bubus.spring.configurator.ContextConfigurator;

@Component
public interface BeanPostProcessor extends ContextConfigurator {
    void construct(Bean bean) throws Exception;
}
