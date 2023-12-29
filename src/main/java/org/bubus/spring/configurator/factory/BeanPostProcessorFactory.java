package org.bubus.spring.configurator.factory;

import org.bubus.spring.bean.BeanDefinition;
import org.bubus.spring.context.InternalContext;
import org.bubus.spring.bean.Bean;
import org.bubus.spring.bpp.BeanPostProcessor;
import org.bubus.spring.exception.BeanNotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.internalContext.getBeanDefinitionsContainer().values().forEach(this::constructBeanDefinitions);
        Set<BeanDefinition> sortedBeanDefinitions = this.internalContext.getBeanDefinitionsContainer().values().stream().sorted(Comparator.comparingInt(BeanDefinition::getOrder)).collect(Collectors.toCollection(LinkedHashSet::new));
        sortedBeanDefinitions.forEach(this::constructBean);
    }

    private void constructBeanDefinitions(BeanDefinition beanDefinition) {
        try {
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                this.internalContext.putBeanDefinition(beanPostProcessor.preConstruct(beanDefinition));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Bean preConstructBean(BeanDefinition beanDefinition) {
        try {
            Object object = beanDefinition.getClazz().getDeclaredConstructor().newInstance();
            return new Bean(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void constructBean(BeanDefinition beanDefinition){
        Bean bean = preConstructBean(beanDefinition);
        try{
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                this.internalContext.putBean(beanPostProcessor.postConstruct(bean));
            }
        } catch (BeanNotFoundException beanNotFoundException){
            Class<?> missingBeanClazz = beanNotFoundException.getMissingBeanClazz();
            Collection<?> missingBeans = this.internalContext.getBeanDefinitionsContainer().getBeansByInterface(missingBeanClazz);
            BeanDefinition mainMissingBean = this.internalContext.getBeanDefinitionsContainer().get(missingBeanClazz);
            if(mainMissingBean != null){
                constructBean(mainMissingBean);
                constructBean(beanDefinition);
                return;
            }
            if(!missingBeans.isEmpty()) {
                for (Object missingBean : missingBeans) {
                    constructBean(this.internalContext.getBeanDefinitionsContainer().get((Class<?>) missingBean));
                }
                constructBean(beanDefinition);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
