package org.bubus.zambara.bean;

public class Bean {
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;

    public Class<?> getClazz() {
        return object.getClass();
    }

    public Bean(Object object){
        this.object = object;
    }

}
