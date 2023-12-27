package org.bubus.command;

import java.io.*;

public class ShowLogsCommand implements Command{
    @Override
    public boolean run(String[] args, String arg) {
        File logs = new File("log4j.log");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logs));
            bufferedReader.lines().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "l";
    }
}
