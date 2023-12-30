package org.bubus.zambara.configurator.factory;

import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bpp.BeanPostProcessor;
import org.bubus.zambara.exception.BeanNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class BeanPostProcessorFactory extends AbstractConfiguratorFactory<BeanPostProcessor> {
    private Set<BeanPostProcessor> beanPostProcessors = new HashSet<>();
    public BeanPostProcessorFactory(InternalContext internalContext){
        super(internalContext);
    }

    @Override
    public void initialize(Set<Class<BeanPostProcessor>> beanPostProcessors) {
        constructBeanPostProcessors(beanPostProcessors);

        this.internalContext
                .getBeanDefinitionsContainer()
                .values()
                .forEach(this::constructBeanDefinitions);

        Set<Bean> sortedBeanDefinitions = this.internalContext
                .getBeanDefinitionsContainer()
                .values()
                .stream()
                .sorted(Comparator
                        .comparingInt(BeanDefinition::getOrder))
                .map(beanDefinition -> preConstructBean(beanDefinition.getClazz()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        sortedBeanDefinitions.forEach(this::constructBean);
    }

    private void constructBeanPostProcessors(Set<Class<BeanPostProcessor>> beanPostProcessorClazzs) {
        for (Class<BeanPostProcessor> beanPostProcessorClazz : beanPostProcessorClazzs) {
            BeanPostProcessor beanPostProcessor = preConstructContextConfigure(beanPostProcessorClazz);
            this.beanPostProcessors.add(beanPostProcessor);
        }

        for (BeanPostProcessor rawBeanPostProcessor : this.beanPostProcessors) {
            constructBean(new Bean((rawBeanPostProcessor)));
        }
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

    private Bean preConstructBean(Class<?> beanDefinitionClazz) {
        try {
            Object object = beanDefinitionClazz.getDeclaredConstructor().newInstance();
            return new Bean(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void constructBean(Bean bean){
        try{
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                this.internalContext.putBean(beanPostProcessor.postConstruct(bean));
            }
        } catch (BeanNotFoundException beanNotFoundException){
            constructMissingBean(beanNotFoundException.getMissingBeanClazz());
            constructBean(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void constructMissingBean(Class<?> missingBeanClazz) {
        Collection<BeanDefinition> missingBeans = this.internalContext.getBeanDefinitionsContainer().getBeanDefinitionsByInterface(missingBeanClazz);
        BeanDefinition mainMissingBean = this.internalContext.getBeanDefinitionsContainer().get(missingBeanClazz);
        if(mainMissingBean != null){
            Bean bean = preConstructBean(mainMissingBean.getClazz());
            constructBean(bean);
            return;
        }
        if(!missingBeans.isEmpty()) {
            for (BeanDefinition missingBean : missingBeans) {
                Bean bean = preConstructBean(missingBean.getClazz());
                constructBean(bean);
            }
            return;
        }
        throw new RuntimeException("No BeanDefinition for class[" + missingBeanClazz + "]");
    }
}
