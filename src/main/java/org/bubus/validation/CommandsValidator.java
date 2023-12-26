package org.bubus.validation;

import java.util.Set;

public class CommandsValidator {
    private Set<Validator> validators;

    public CommandsValidator(Set<Validator> validators){
        this.validators = validators;
    }

    /*
    * Return null if all commands is valid otherwise return first not valid command
    * */
    public String validate(String[] commands){
        for (String command : commands) {
            for (Validator validator : validators) {
                if(!validator.validate(command))
                    return validator.getErrorMessage();
            }
        }
        return null;
    }
}
