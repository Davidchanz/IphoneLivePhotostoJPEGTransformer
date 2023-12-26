package org.bubus.validation;

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
