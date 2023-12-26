package org.bubus;

import org.apache.log4j.Logger;
import org.bubus.command.CommandsResolver;
import org.bubus.validation.CommandsValidator;

public class Main {
    static final Logger logger = Logger.getLogger(Transformer.class);
    public static void main(String[] args) {
        Context context = new Context(Main.class, args);
        CommandsResolver commandsResolver = new CommandsResolver(context.getCommands());
        CommandsValidator commandsValidator = new CommandsValidator(context.getValidators());

        String errorMessage = commandsValidator.validate(args);
        if(errorMessage != null){
            showErrorMessage(errorMessage);
        }
        commandsResolver.resolveCommands(args);

        /*
        * TODO
        *  добавить функцию котрая добавляет оригинальную дату ко всем фотографиям и видео из META File Modify Date
        * */

        /*Transformer transformer = new Transformer();
        if(args.length == 1)
            transformer.transform(args[0]);
        else
            transformer.transform(System.getProperty("user.dir"));*/
    }

    private static void showErrorMessage(String message) {
        logger.error(message);
        System.out.println(message);
        System.exit(1);
    }
}