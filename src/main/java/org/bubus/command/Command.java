package org.bubus.command;

import java.util.Arrays;
import java.util.Set;

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
    default <R> R getCommandResult(){
        return null;
    }
    default void initializeOptionalCommands(String... args){
        Set<Command> optionalCommands = getOptionalCommands();
        if(!optionalCommands.isEmpty()) {
            for (Command optionalCommand : optionalCommands) {
                for (int i = 0; i < args.length; i++) {
                    if (optionalCommand.isSupport(args[i])) {
                        optionalCommand.initializeOptionalCommands(Arrays.copyOfRange(args, i + 1, args.length));
                        break;
                    }
                }
            }
        }else {
            setCommandArgument(args);
        }
    }

    default void setCommandArgument(String... args){};

    default Set<Command> getOptionalCommands(){return Set.of();};
    boolean isRunnable();
    void setOrder(int order);
    int getOrder();
}
