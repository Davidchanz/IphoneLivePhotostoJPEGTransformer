package org.bubus.command.runnable;

import org.bubus.ConsoleDialog;
import org.bubus.Transformer;
import org.bubus.command.AbstractCommand;
import org.bubus.command.Command;
import org.bubus.command.option.PathCommand;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;

import java.util.Set;

@Component
public class AdjustOriginalCreatedDateCommand extends AbstractCommand {
    @Autowired
    private PathCommand pathCommand;
    @Autowired
    private ConsoleDialog consoleDialog;

    @Autowired
    private Transformer transformer;
    @Override
    public boolean run(String[] args, String arg) {
        if (this.pathCommand.accept(args))
            this.transformer.adjustOriginalCreatedTime(this.pathCommand.getCommandResult());
        else {
            String currentDirectory = System.getProperty("user.dir");
            boolean yes = this.consoleDialog.askYesNo(
                    "Path which you specified is not valid there is no Directory " +
                            "[" + this.pathCommand.getPath() + "] \n" +
                            "Do you want execute command [" + this.getCommandName() + "] " +
                            "in current Directory [" + currentDirectory + "]?");
            if(yes)
                this.transformer.livePhotoTransform(currentDirectory);
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "acd";
    }

    @Override
    public boolean isRunnable() {
        return true;
    }

    @Override
    public Set<Command> getOptionalCommands() {
        return Set.of(pathCommand);
    }
}
