package org.bubus;

import org.bubus.context.Context;

public class Main {

    public static void main(String[] args) {
        Context context = new Context(Main.class, args);
        Program program = context.getBean(Program.class);
        program.process(args);
    }
}