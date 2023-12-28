package org.bubus.spring.bpp;

import org.bubus.spring.bean.Bean;
import org.bubus.spring.context.InternalContext;
import org.bubus.spring.annotation.Autowired;
import org.bubus.spring.annotation.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutowiredBeanPostProcessor implements BeanPostProcessor{
    private InternalContext internalContext;

    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void construct(Bean bean) throws Exception{
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
                        declaredField.setAccessible(true);
                        declaredField.set(object, beans);
                    }else {
                        Object injectBean = internalContext.getBean(fieldType);
                        declaredField.setAccessible(true);
                        declaredField.set(object, injectBean);
                    }
                }
            }
        }
    }

}
