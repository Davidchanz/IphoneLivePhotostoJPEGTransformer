package org.bubus.spring.bean;

import java.util.Comparator;

public class BeanDefinition implements IoCContainerItem {
    private Class<?> clazz;
    private String scope;
    private String id;

    private int order = Integer.MAX_VALUE;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object getObject() {
        return getClazz();
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public BeanDefinition(){
        this.scope = "Singleton";
    }
}
