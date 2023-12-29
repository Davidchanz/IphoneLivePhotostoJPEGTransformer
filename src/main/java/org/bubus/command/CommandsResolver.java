package org.bubus.command;

import org.bubus.spring.annotation.Autowired;
import org.bubus.spring.annotation.Component;

import java.util.Arrays;
import java.util.Set;

@Component
public class CommandsResolver {
    @Autowired
    private Set<Command> commands;
    public void resolveCommands(String[] args){//TODO    -lpt -p /first/dir -acd -p /second/dir
        for(int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {//Command //TODO resolve by validators
                args[i] = args[i].substring(1);
                for (Command command : commands) {
                    if(command.isSupport(args[i])){
                        initializeCommandArguments(command, i, args);
                        break;
                    }
                }
            }
        }
        for (Command command : commands) {
            command.accept(args);
        }
    }

    private void initializeCommandArguments(Command command, int currentCommandIndex, String[] args) {
        if(args[currentCommandIndex].startsWith("-")){
            for (Command optionCommand : command.getOptionsCommands(commands).values()) {
                if(args[currentCommandIndex].substring(1).equals(optionCommand.getCommandName())){
                    optionCommand.setCommandsArguments(Arrays.copyOfRange(args, currentCommandIndex+1, args.length));
                }
            }
        }else {
            command.setCommandsArguments(Arrays.copyOfRange(args, currentCommandIndex+1, args.length));
        }
    }
}
