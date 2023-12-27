package org.bubus.validation;

import org.bubus.context.annotation.Component;

@Component
public class PrefixMinusCommandValidator implements Validator{
    @Override
    public boolean validate(String command) {
        return command.startsWith("-");
    }

    @Override
    public String getErrorMessage() {
        return "Command must start form '-'";
    }
}
