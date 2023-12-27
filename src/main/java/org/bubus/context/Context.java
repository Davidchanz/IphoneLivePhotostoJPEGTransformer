package org.bubus.context;

import org.bubus.context.annotation.Component;
import org.reflections.Reflections;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Context {
    private final String rootPath;
    private Map<String, Class<?>> targetContainer = new HashMap<>();
    private Map<String, Object> IoC = new HashMap<>();
    private Set<BeanPostProcessor> beanConstructors;
    private boolean isContextStarted = false;

    private Context(Class<?> configClass){
        this.rootPath = configClass.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        scan(configClass);
        beanConstructors = getBeanConstructors();
        filterBeanPostConstructors();
        preConstructBeans();
        beansConstruct();
        this.isContextStarted = true;
    }

    public Context(Class<?> configClass, String[] args){
        this(configClass);
    }

    private void preConstructBeans() {
        this.targetContainer.values().forEach(aClass -> {
            try {
                Object bean = aClass.getDeclaredConstructor().newInstance();
                this.IoC.put(getBeanKey(aClass), bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> T getBean(Class<T> clazz){
        String key = getBeanKey(clazz);
        Object bean = this.IoC.get(key);
        return (T) bean;
    }

    private static <T> String getBeanKey(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        String key = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        return key;
    }

    public <T> Set<T> getBeans(Class<T> clazz) {
        String key = getBeanKey(clazz);
        Set<T> beans = new HashSet<>();
        for (Object bean : this.IoC.values()) {
            for (Class<?> anInterface : bean.getClass().getInterfaces()) {
                Set<Class<?>> interfaces = new HashSet<>();
                interfaces.addAll(Arrays.stream(bean.getClass().getInterfaces()).toList());
                findSubInterfaces(interfaces, anInterface, clazz);
                for (Class<?> aClass : interfaces) {
                    if(aClass.equals(clazz)){
                        beans.add((T) bean);
                    }
                }
            }
        }
        if(beans.isEmpty())
            new RuntimeException("Bean with id [" + key + "] not exist!");
        return beans;
    }

    private void findSubInterfaces(Set<Class<?>> container, Class<?> anInterface, Class<?> targetInterface) {
        Class<?>[] interfaces = anInterface.getInterfaces();
        for (Class<?> aClass : interfaces) {
            findSubInterfaces(container, aClass, targetInterface);
            container.add(aClass);
        }
    }

    private void beansConstruct() {
        this.IoC.keySet().forEach(this::constructBean);
    }

    private void constructBean(String key){
        try{
            Class<?> aClass = this.targetContainer.get(key);
            Object bean = this.IoC.get(key);
            for (BeanPostProcessor beanPostProcessor : beanConstructors) {
                bean = beanPostProcessor.construct(bean, aClass);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<BeanPostProcessor> getBeanConstructors() {
        Set<BeanPostProcessor> beans = new HashSet<>();
        findInheritances(BeanPostProcessor.class).forEach(aClass -> {
            try {
                beans.add(aClass.getDeclaredConstructor(Context.class).newInstance(this));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return beans;
    }

    private <T> Set<Class<T>> findInheritances(Class<T> target) {
        Set<Class<T>> targetCollection = new HashSet<>();
        for (Class<?> clazz : targetContainer.values()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(target)){
                    targetCollection.add((Class<T>)clazz);
                }
            }
        }
        return targetCollection;
    }

    private void filterBeanPostConstructors(){
        Set<String> keys = new HashSet<>();
        this.targetContainer.forEach((s, aClass) -> {
            for (Class<?> anInterface : aClass.getInterfaces()) {
                if (anInterface.equals(BeanPostProcessor.class)) {
                    keys.add(s);
                    break;
                }
            }
        });
        for (String key : keys) {
            this.targetContainer.remove(key);
        }
    }

    private void scan(Class<?> configClass) {
        Set<String> allPackages = findAllSubPackages(configClass.getPackageName());
        allPackages.add(configClass.getPackageName());
        for (String subPackage : allPackages) {
            Set<Class<?>> allClassesUsingClassLoader = findAllClassesUsingClassLoader(subPackage);
            for (Class<?> aClass : allClassesUsingClassLoader) {
                String key = getBeanKey(aClass);
                targetContainer.put(key, aClass);
            }
        }
    }

    private Set<String> findAllSubPackages(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        Set<String> packages = new HashSet<>();
        for (String line : reader.lines().toList()) {
            File file = new File(
                    rootPath +
                            packageName.replaceAll("[.]", "/") +
                            "/" +
                            line
            );
            if(file.isDirectory()){
                String _package = packageName + "." + line;
                packages.add(_package);
                packages.addAll(findAllSubPackages(packageName + "." + line));
            }
        }
        return packages;
    }

    private Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        try {
            Class<?> aClass = Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
            if(filterTarget(aClass))
                return aClass;
            else
                return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class instance error [" + packageName + "."
                    + className.substring(0, className.lastIndexOf('.')) + "]");
        }
    }

    private boolean filterTarget(Class<?> aClass) {
        Set<Component> annotations = new HashSet<>();
        findInterfacesAnnotation(annotations, aClass, Component.class);
        return (!annotations.isEmpty() && !aClass.isInterface());
    }

    private <T extends Annotation> T findInterfacesAnnotation(Set<T> annotations, Class<?> aClass, Class<T> annotation) {
        for (Class<?> anInterface : aClass.getInterfaces()) {
            findInterfacesAnnotation(annotations, anInterface, annotation);
        }
        T declaredAnnotation = aClass.getDeclaredAnnotation(annotation);
        if(declaredAnnotation != null) {
            annotations.add(declaredAnnotation);
            return declaredAnnotation;
        }
        return null;
    }

    boolean isContextStarted() {
        return isContextStarted;
    }
}
