package org.bubus.spring.bean;

public class Bean implements IoCContainerItem {
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;
    @Override
    public Class<?> getClazz() {
        return object.getClass();
    }

    public Bean(Object object){
        this.object = object;
    }

}
