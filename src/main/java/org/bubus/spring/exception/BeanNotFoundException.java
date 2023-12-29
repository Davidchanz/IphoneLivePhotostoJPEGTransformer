package org.bubus.spring.exception;

public class BeanNotFoundException extends RuntimeException{
    private Class<?> beanClazz;
    public BeanNotFoundException(String msg, Class<?> beanClazz){
        super(msg);
        this.beanClazz = beanClazz;
    }

    public Class<?> getMissingBeanClazz(){
        return beanClazz;
    }
}
