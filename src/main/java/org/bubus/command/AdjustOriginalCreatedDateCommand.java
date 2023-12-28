package org.bubus.command;

import org.bubus.spring.annotation.Component;

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
