package org.bubus.command;

import org.bubus.Transformer;
import org.bubus.context.annotation.Component;

@Component
public class LivePhotoTransformCommand implements Command{

    /*
    TODO
     scan args if contains command name accept
     if args like path example [java -jar Program.jar -lpt /path/to/dir]
     if path require arg validate it in validator такая хрень не должна попасть сюда
     получить все обязательные параметры и проверить не обязательные порядок не важен
     проверять вплоть до следуюзей команды
     нужна команда -lpt /path/to/dir -r
     -r необязательная команда
     если она есть то удалять MOV если нет оставлять
    */
    @Override
    public boolean accept(String args[]) {
        Transformer transformer = new Transformer();
        if(args.length == 1)
            transformer.transform(args[0]);
        else
            transformer.transform(System.getProperty("user.dir"));
        return true;
    }

    @Override
    public String getCommandName() {
        return "lpt";
    }
}
