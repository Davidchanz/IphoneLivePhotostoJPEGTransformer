package org.bubus.context;

import org.bubus.context.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class AutowiredBeanPostProcessor implements BeanPostProcessor{
    private Context context;

    public AutowiredBeanPostProcessor(){

    }

    public AutowiredBeanPostProcessor(Context context){
        this.context = context;
    }
    @Override
    public Object construct(Object bean, Class<?> clazz) throws Exception{
        for (Field declaredField : clazz.getDeclaredFields()) {
            for (Annotation declaredAnnotation : declaredField.getDeclaredAnnotations()) {
                if(declaredAnnotation.annotationType().equals(Autowired.class)){
                    Class<?> fieldType = declaredField.getType();
                    if(Arrays.stream(fieldType.getInterfaces()).toList().contains(Collection.class) ){
                        ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
                        Type actualTypeArgument = genericType.getActualTypeArguments()[0];
                        Set<?> beans = context.getBeans(Class.forName(actualTypeArgument.getTypeName()));
                        declaredField.setAccessible(true);
                        declaredField.set(bean, beans);
                    }else {
                        Object injectBean = context.getBean(fieldType);
                        declaredField.setAccessible(true);
                        declaredField.set(bean, injectBean);
                    }
                }
            }
        }
        return bean;
    }
}
