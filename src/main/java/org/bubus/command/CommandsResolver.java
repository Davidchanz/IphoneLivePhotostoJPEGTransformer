package org.bubus.command;

import java.util.Set;

public class CommandsResolver {
    public static final String COMMAND_PREFIX = "-";
    private Set<Command> commands;
    public CommandsResolver(Set<Command> commands){
        this.commands = commands;
    }
    public boolean resolveCommands(String args[]){
        for (Command command : commands) {
            if(command.accept(args))
                return true;
        }
        return false;
    }
}
