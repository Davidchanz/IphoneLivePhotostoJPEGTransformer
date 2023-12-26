package org.bubus.command;

public interface Command {
    boolean accept(String arg[]);
    String getCommandName();
}
