package org.bubus.command.runnable;

import org.bubus.command.AbstractCommand;
import org.bubus.command.Command;
import org.bubus.zambara.annotation.Component;

import java.io.*;
import java.util.Set;

@Component
public class ShowLogsCommand extends AbstractCommand {
    @Override
    public boolean run(String[] args, String arg) {
        /*File logs = new File("log4j.log");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logs));
            bufferedReader.lines().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println("Logging...");
        return true;
    }

    @Override
    public String getCommandName() {
        return "l";
    }

    @Override
    public boolean isRunnable() {
        return false;
    }

}
