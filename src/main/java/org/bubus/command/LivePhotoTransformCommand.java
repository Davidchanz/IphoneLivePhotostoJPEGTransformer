package org.bubus.command;

import org.bubus.Transformer;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;

import java.util.List;
import java.util.Set;

@Component
public class LivePhotoTransformCommand implements Command{

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
    private PathCommand command;

    @Override
    public boolean run(String[] args, String arg) {
        CommandDefinitionMap optionsCommands = getOptionsCommands(command);
        PathCommand pathCommand = optionsCommands.get(PathCommand.class);
        Transformer transformer = new Transformer();
        if (pathCommand.accept(args))
            transformer.transform(pathCommand.getCommandResult());
        else
            transformer.transform(System.getProperty("user.dir"));
        return true;
    }

    @Override
    public Set<Class<? extends Command>> getOptionsCommandsClasses() {
        return Set.of(PathCommand.class);
    }

    @Override
    public String getCommandName() {
        return "lpt";
    }
}
