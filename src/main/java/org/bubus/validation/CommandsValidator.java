package org.bubus.validation;

import org.bubus.context.annotation.Autowired;
import org.bubus.context.annotation.Component;

import java.util.Set;

@Component
public class CommandsValidator {
    @Autowired
    private Set<Validator> validators;

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
