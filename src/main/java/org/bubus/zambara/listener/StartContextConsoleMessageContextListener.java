package org.bubus.zambara.listener;

import org.bubus.zambara.context.InternalContext;
import org.bubus.zambara.context.state.ContextStarted;

public class StartContextConsoleMessageContextListener implements ContextListener<ContextStarted>{
    private InternalContext internalContext;
    @Override
    public void config(InternalContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void listen() {//logo Zambara
        String logo =
                """
                                ----     |     |-   -|  |---|     |    |---|     |   
                                  -     | |    | - - |  |   |    | |   |   |    | |  
                                 -     |---|   |  -  |  |---|   |---|  |---|   |---| 
                                ----  |     |  |     |  |___|  |     | |      |     |
                """;
        System.out.println(logo);
    }
}
