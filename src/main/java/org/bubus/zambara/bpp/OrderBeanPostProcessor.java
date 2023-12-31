package org.bubus.zambara.bpp;

import org.bubus.command.runnable.AdjustOriginalCreatedDateCommand;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.annotation.Order;
import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanDefinition;

@Component
public class OrderBeanPostProcessor implements BeanPostProcessor{
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
