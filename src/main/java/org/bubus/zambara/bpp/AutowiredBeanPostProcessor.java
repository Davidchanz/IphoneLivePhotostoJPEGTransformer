package org.bubus.zambara.bpp;

import org.bubus.zambara.bean.Bean;
import org.bubus.zambara.bean.BeanDefinition;
import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.utils.CollectionResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

@Component
public class AutowiredBeanPostProcessor implements BeanPostProcessor{
    private InternalContext internalContext;

    @Autowired
    private CollectionResolver collectionResolver;

    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public BeanDefinition preConstruct(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition;
    }

    @Override
    public Bean postConstruct(Bean bean) throws Exception{
        Class<?> clazz = bean.getClazz();
        Object object = bean.getObject();
        for (Field declaredField : clazz.getDeclaredFields()) {
            for (Annotation declaredAnnotation : declaredField.getDeclaredAnnotations()) {
                if(declaredAnnotation.annotationType().equals(Autowired.class)){
                    Class<?> fieldType = declaredField.getType();
                    if(Arrays.stream(fieldType.getInterfaces()).toList().contains(Collection.class)){
                        ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
                        Type actualTypeArgument = genericType.getActualTypeArguments()[0];
                        Collection<?> beans = internalContext.getBeans(Class.forName(actualTypeArgument.getTypeName()));
                        CollectionResolver collectionResolver = new CollectionResolver();
                        Object collection = collectionResolver.convertToFieldCollection(beans, fieldType);
                        declaredField.setAccessible(true);
                        declaredField.set(object, collection);
                    }else {
                        Object injectBean = internalContext.getBean(fieldType);
                        declaredField.setAccessible(true);
                        declaredField.set(object, injectBean);
                    }
                }
            }
        }
        return bean;
    }

}
