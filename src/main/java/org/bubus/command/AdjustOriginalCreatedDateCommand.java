package org.bubus.command;

import org.bubus.context.annotation.Component;

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
}
