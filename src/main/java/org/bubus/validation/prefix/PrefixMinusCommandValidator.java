package org.bubus.validation.prefix;

import org.bubus.zambara.annotation.Component;

@Component
public class PrefixMinusCommandValidator implements PrefixValidator {
    private final String COMMAND_PREFIX_MINUS = "-";
    @Override
    public boolean validate(String command) {
        return command.startsWith(COMMAND_PREFIX_MINUS);
    }

    @Override
    public String getErrorMessage() {
        return "Command must start form '" + COMMAND_PREFIX_MINUS + "'";
    }

    @Override
    public String getCommand(String arg) {
        return arg.substring(1);
    }
}
