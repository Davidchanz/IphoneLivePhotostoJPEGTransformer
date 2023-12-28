package org.bubus;

import org.bubus.command.CommandsResolver;
import org.bubus.spring.annotation.Autowired;
import org.bubus.spring.annotation.Component;
import org.bubus.validation.CommandsValidator;

@Component
public class Program {
    @Autowired
    private CommandsResolver commandsResolver;
    @Autowired
    private CommandsValidator commandsValidator;
    @Autowired
    private ErrorShower errorShower;
    public void process(String[] args){
        /*String validateMessage = commandsValidator.validate(args);
        if(validateMessage != null){
            errorShower.showErrorMessage(validateMessage);
            System.exit(1);
        }else {
        }*/
        commandsResolver.resolveCommands(args);
        /*
         * TODO
         *  добавить функцию котрая добавляет оригинальную дату ко всем фотографиям и видео из META File Modify Date
         * */
    }
}
