package org.bubus.context;

import org.bubus.context.annotation.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class Context {
    private final String rootPath;
    private Map<String, Class<?>> targetContainer = new HashMap<>();
    private Map<String, Object> IoC = new HashMap<>();

    private Context(Class<?> configClass){
        this.rootPath = configClass.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        scan(configClass);
        beansConstruct();
    }

    public Context(Class<?> configClass, String[] args){
        this(configClass);
    }

    public <T> T getIoCBean(Class<T> clazz){
        String simpleName = clazz.getSimpleName();
        String key = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        return (T) this.IoC.get(key);
    }

    public <T> Set<T> getBeans(Class<T> clazz) {
        Set<T> beans = new HashSet<>();
        findInheritances(clazz).forEach(aClass -> {
            try {
                beans.add(aClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return beans;
    }

    public <T> T getBean(Class<T> clazz) {
        Set<T> beans = new HashSet<>();
        findInheritances(clazz).forEach(aClass -> {
            try {
                beans.add(aClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        if(beans.size() > 1)
            throw new RuntimeException("There are more than one Beans type of [" + clazz.getName() + "] {" + beans.toString() + "}");
        return beans.iterator().next();
    }

    private void beansConstruct() {
        Set<BeanPostProcessor> beanConstructors = getBeanConstructors();
        filterBeanPostConstructors();
        this.targetContainer.forEach((s, aClass) -> {
            for (BeanPostProcessor beanPostProcessor : beanConstructors) {
                Object bean = beanPostProcessor.construct(aClass);
                this.IoC.put(s, bean);
            }
        });
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
                String simpleName = aClass.getSimpleName();
                String key = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
                targetContainer.put(key, aClass);
            }
        }
    }

    public Set<String> findAllSubPackages(String packageName) {
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

    public Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Class<?> getClass(String className, String packageName) {
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
}
