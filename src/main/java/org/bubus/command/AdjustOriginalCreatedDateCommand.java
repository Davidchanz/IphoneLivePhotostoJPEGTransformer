package org.bubus.command;

import org.bubus.zambara.annotation.Component;

import java.util.Set;

@Component
public class AdjustOriginalCreatedDateCommand implements Command{

    @Override
    public boolean run(String[] args, String arg) {
        return false;
    }

    @Override
    public String getCommandName() {
        return "acd";
    }

    @Override
    public Set<Class<? extends Command>> getOptionsCommandsClasses() {
        return Set.of(PathCommand.class);
    }
}
