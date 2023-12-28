package org.bubus.command;

import org.bubus.spring.annotation.Component;

import java.io.File;

@Component
public class PathCommand implements Command{
    private String path;
    @Override
    public boolean run(String[] args, String arg) {
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()){
            //TODO default throw ошибка обработки команды
        } else {
            //TODO default throw ошибка обработки команды
            return true;
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "p";
    }

    @Override
    public String getCommandResult() {
        return path;
    }

    @Override
    public void setCommandsArguments(String... args) {
        this.path = args[0];
    }
}
