package org.bubus.command.option;

import org.bubus.command.AbstractCommand;
import org.bubus.command.Command;
import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.annotation.Scope;

import java.io.File;
import java.util.Set;

@Component
@Scope("prototype")
public class PathCommand extends AbstractCommand {
    private String path;
    private String errorMessageText;

    @Override
    public boolean run(String[] args, String arg) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
    public String getErrorMessageText(){
        return this.errorMessageText;
    }
    public String getPath() {
        return path;
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
    public boolean isRunnable() {
        return false;
    }

    @Override
    public void setCommandArgument(String... args) {
        this.path = args[0];
    }
}
