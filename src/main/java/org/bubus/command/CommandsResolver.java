package org.bubus.command;

import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

@Component
public class CommandsResolver {
    @Autowired
    private Set<Command> commands;
    public void resolveCommands(String[] args){//TODO -lpt -p /first/dir -acd -p /second/dir
        int order = 0;
        for(int i = 0; i < args.length; i++) {
            for (Command command : commands) {
                if(command.isSupport(args[i])){
                    command.initializeOptionalCommands(Arrays.copyOfRange(args, i+1, args.length));
                    command.setOrder(order++);
                    break;
                }
            }
        }
        for (Command command : commands.stream().filter(command -> command.isRunnable())
                .sorted(
                        Comparator.comparingInt(Command::getOrder)
                ).toList()) {
            command.accept(args);
        }
    }

    /*private void initializeCommandArguments(Command command, int currentCommandIndex, String[] args) {
        if(args[currentCommandIndex].startsWith("-")){
            if(args[currentCommandIndex].substring(1).equals(command.getCommandName())){
                command.setCommandsArguments(Arrays.copyOfRange(args, currentCommandIndex+1, args.length));
            }
        }else {
            command.setCommandsArguments(Arrays.copyOfRange(args, currentCommandIndex+1, args.length));
        }
    }*/
}
