package org.bubus.validation;

import org.bubus.command.Command;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;

import java.util.Set;

@Component
public class CommandsValidator {
    @Autowired
    private Set<Validator> validators;

    @Autowired
    private Set<Command> commands;

    /*
    * TODO Return null if all commands is valid otherwise return first not valid command
    * */
    public String validate(String[] args){
        for(int i = 0; i < args.length; i++) {
            for (Command command : commands) {
                if(command.isSupport(args[i].substring(1))){
                    for (Validator validator : validators) {
                        if(!validator.validate(args[i]))
                            return validator.getErrorMessage();
                        else
                            args[i] = validator.process(args[i]);
                    }
                    break;
                }
            }
        }
        return null;
    }
}
