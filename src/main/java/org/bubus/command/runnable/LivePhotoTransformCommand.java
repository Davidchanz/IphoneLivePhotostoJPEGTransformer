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
public class LivePhotoTransformCommand extends AbstractCommand {

    /*
    TODO
     scan args if contains command name accept
     if args like path example [java -jar Program.jar -lpt -p /path/to/dir -i]
     if path require arg validate it in validator такая хрень не должна попасть сюда
     получить все обязательные параметры и проверить не обязательные порядок не важен
     проверять вплоть до следуюзей команды
     нужна команда -lpt /path/to/dir -r
     -r необязательная команда
     если она есть то удалять MOV если нет оставлять
    */

    @Autowired
    private PathCommand pathCommand;

    @Autowired
    private ShowLogsCommand showLogsCommand;

    @Autowired
    private ConsoleDialog consoleDialog;

    @Autowired
    private Transformer transformer;

    @Override
    public boolean run(String[] args, String arg) {
        if (this.pathCommand.accept(args))
            this.transformer.livePhotoTransform(this.pathCommand.getCommandResult());
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
    public Set<Command> getOptionalCommands() {
        return Set.of(pathCommand, showLogsCommand);
    }

    @Override
    public String getCommandName() {
        return "lpt";
    }

    @Override
    public boolean isRunnable() {
        return true;
    }
}
