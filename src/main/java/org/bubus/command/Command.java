package org.bubus.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Command {
    default boolean accept(String[] args){
        for (String arg : args) {
            if (isSupport(arg)) {
                return run(args, arg);
            }
        }
        return false;
    }
    boolean run(String[] args, String arg);
    String getCommandName();
    default boolean isSupport(String arg){
        return arg.equals(getCommandName());
    }
    default CommandDefinitionMap getOptionsCommands(Command... commands){
        CommandDefinitionMap optionsCommands = new CommandDefinitionMap();
        for (Class<? extends Command> optionsCommand : this.getOptionsCommandsClasses()) {
            for (Command command : commands) {
                if(command.getClass().equals(optionsCommand)){
                    optionsCommands.put(optionsCommand, getCommand(command, optionsCommand));
                }
            }
        }
        return optionsCommands;
    }
    default Set<Class<? extends Command>> getOptionsCommandsClasses(){
        return new HashSet<>();
    }
    default <T> T getCommand(Command command, Class<T> commandClass){
        return (T) command;
    }
    default <R> R getCommandResult(){
        return null;
    }

    class CommandDefinitionMap extends HashMap<Class<? extends Command>, Command>{
        public <T extends Command> T get(Class<T> key) {
            return (T) super.get(key);
        }
    }

    default void setCommandsArguments(String... args){

    }
}
