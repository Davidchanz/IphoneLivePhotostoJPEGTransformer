package org.bubus.spring.reader;

import org.bubus.spring.context.ApplicationInternalContext;
import org.bubus.spring.bean.BeanDefinition;
import org.bubus.spring.bean.IoCContainer;
import org.bubus.spring.annotation.Component;
import org.bubus.spring.context.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.*;
import java.util.stream.Collectors;

public class ClassPathBeanDefinitionReader implements BeanDefinitionReader{
    private String rootPath;
    private Class<?> configClass;
    private final IoCContainer beanDefinitionsContainer = new IoCContainer();
    public ClassPathBeanDefinitionReader(Class<?> configClass){
        this.configClass = configClass;
        this.rootPath = configClass.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
    }
    @Override
    public Context run(){
        return new ApplicationInternalContext(scan());
    }

    @Override
    public IoCContainer<BeanDefinition> scan() {
        Set<String> allPackages = findAllSubPackages(configClass.getPackageName());
        allPackages.add(configClass.getPackageName());
        for (String subPackage : allPackages) {
            Set<Class<?>> allClassesUsingClassLoader = findAllClassesUsingClassLoader(subPackage);
            for (Class<?> aClass : allClassesUsingClassLoader) {
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setClazz(aClass);
                beanDefinitionsContainer.put(beanDefinition);
            }
        }
        return beanDefinitionsContainer;
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
        Set<Class<?>> annotations = new HashSet<>();
        findAllSubClassesAnnotated(annotations, aClass, Component.class);
        return (!annotations.isEmpty());
    }

    private <T extends Annotation> void findAllSubClassesAnnotated(Set<Class<?>> classes, Class<?> aClass, Class<T> annotation) {
        for (Class<?> anInterface : aClass.getInterfaces()) {
            findAllSubClassesAnnotated(classes, anInterface, annotation);
        }
        T aClassDeclaredAnnotation = aClass.getDeclaredAnnotation(annotation);
        if(aClassDeclaredAnnotation != null){
            classes.add(aClass);
        }else {
            for (Annotation declaredAnnotation : aClass.getDeclaredAnnotations()) {
                if (declaredAnnotation != null) {
                    boolean isClassHasAnnotation = findAllSubAnnotationAnnotations(declaredAnnotation, annotation);
                    if (isClassHasAnnotation) {
                        classes.add(aClass);
                    }
                }
            }
        }
    }

    private <T extends Annotation> boolean findAllSubAnnotationAnnotations(Annotation aClass, Class<T> annotation) {
        for (Annotation anAnnotation : aClass.annotationType().getDeclaredAnnotations()) {
            if(anAnnotation.annotationType().equals(Documented.class)
                    || anAnnotation.annotationType().equals(Retention.class)
                    || anAnnotation.annotationType().equals(Target.class)){
                continue;
            }
            if(findAllSubAnnotationAnnotations(anAnnotation, annotation))
                return true;
        }
        T declaredAnnotation = aClass.annotationType().getDeclaredAnnotation(annotation);
        if(declaredAnnotation != null) {
            return true;
        }
        return false;
    }
}
