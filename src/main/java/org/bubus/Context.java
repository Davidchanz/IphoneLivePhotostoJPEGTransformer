package org.bubus;

import org.bubus.command.Command;
import org.bubus.validation.Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Context {
    private final String rootPath;
    private Map<String, Class<?>> targetContainer = new HashMap<>();
    private Set<Class<?>> validators = new HashSet<>();
    private Set<Class<?>> commands = new HashSet<>();

    public Set<Validator> getValidators() {
        return validators.stream().map(aClass -> {
            try {
                return (Validator) aClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    public Set<Command> getCommands() {
        return commands.stream().map(aClass -> {
            try {
                return (Command) aClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    private Context(Class<?> configClass){
        this.rootPath = configClass.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
        scan(configClass);
        findAndPut(Validator.class, validators);
        findAndPut(Command.class, commands);

        System.out.println("test");
    }

    public Context(Class<?> configClass, String[] args){
        this(configClass);
    }

    private void findAndPut(Class<?> target, Set<Class<?>> targetCollection) {
        for (Class<?> clazz : targetContainer.values()) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if(anInterface.equals(target)){
                    targetCollection.add(clazz);
                }
            }
        }
    }

    private void scan(Class<?> configClass) {
        Set<String> allPackages = findAllSubPackages(configClass.getPackageName());
        allPackages.add(configClass.getPackageName());
        for (String subPackage : allPackages) {
            Set<Class> allClassesUsingClassLoader = findAllClassesUsingClassLoader(subPackage);
            for (Class aClass : allClassesUsingClassLoader) {
                String simpleName = aClass.getSimpleName();
                String key = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
                targetContainer.put(key, aClass);
            }
        }
        System.out.println(targetContainer);
    }

    public Set<String> findAllSubPackages(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        Set<String> packages = new HashSet<>();
        for (String line : reader.lines().collect(Collectors.toList())) {
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

    public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }
}
