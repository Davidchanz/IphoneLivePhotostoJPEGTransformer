package org.bubus;

import org.bubus.zambara.context.Context;
import org.bubus.zambara.reader.ClassPathBeanDefinitionReader;

public class Main {

    public static void main(String[] args) {
        Context context = new ClassPathBeanDefinitionReader(Main.class).run();
        Program program = context.getBean(Program.class);
        program.process(args);
    }
}