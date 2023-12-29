package org.bubus.spring.bpp;

import org.bubus.command.AdjustOriginalCreatedDateCommand;
import org.bubus.spring.annotation.Autowired;
import org.bubus.spring.annotation.Component;
import org.bubus.spring.annotation.Order;
import org.bubus.spring.bean.Bean;
import org.bubus.spring.bean.BeanDefinition;

@Component
public class OrderBeanPostProcessor implements BeanPostProcessor{
    @Autowired
    private AdjustOriginalCreatedDateCommand command; //TODO пост процессоры берутся не из контекста поэтому после конфигурации, не сохраняются
    @Override
    public BeanDefinition preConstruct(BeanDefinition beanDefinition) throws Exception {
        Class<?> clazz = beanDefinition.getClazz();
        Order orderAnnotation = clazz.getDeclaredAnnotation(Order.class);
        if(orderAnnotation != null){
            int order = orderAnnotation.value();
            beanDefinition.setOrder(order);
        }
        return beanDefinition;
    }

    @Override
    public Bean postConstruct(Bean bean) throws Exception{
        return bean;
    }
}
