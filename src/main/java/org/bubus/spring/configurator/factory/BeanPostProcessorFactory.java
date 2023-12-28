package org.bubus.spring.configurator.factory;

import org.bubus.spring.context.InternalContext;
import org.bubus.spring.bean.Bean;
import org.bubus.spring.bpp.BeanPostProcessor;

import java.util.Set;

public class BeanPostProcessorFactory implements ConfiguratorFactory<BeanPostProcessor> {
    private InternalContext internalContext;
    private Set<BeanPostProcessor> beanPostProcessors;
    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void process(Set<BeanPostProcessor> beanPostProcessors) {
        this.beanPostProcessors = beanPostProcessors;
        preConstructBeans();
        beansConstruct();
    }

    private void preConstructBeans() {
        this.internalContext.getBeanDefinitionsContainer().values().forEach(beanDefinition -> {
            try {
                Object object = beanDefinition.getClazz().getDeclaredConstructor().newInstance();
                Bean bean = new Bean(object);
                this.internalContext.addBean(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void constructBean(Bean bean){
        try{
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                beanPostProcessor.construct(bean);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void beansConstruct() {
        this.internalContext.getBeansContainer().values().forEach(this::constructBean);
    }
}
