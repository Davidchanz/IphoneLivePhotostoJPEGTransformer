package org.bubus.command;

import org.bubus.context.annotation.Autowired;
import org.bubus.context.annotation.Component;

import java.util.Set;

@Component
public class CommandsResolver {
    public static final String COMMAND_PREFIX = "-";
    @Autowired
    private Set<Command> commands;
    public boolean resolveCommands(String[] args){
        for (Command command : commands) {
            if(command.accept(args))
                return true;
        }
        return false;
    }
}
