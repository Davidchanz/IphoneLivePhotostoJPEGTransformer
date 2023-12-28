package org.bubus;

import org.apache.log4j.Logger;
import org.bubus.spring.annotation.Component;

@Component
public class ErrorShower {
    static final Logger logger = Logger.getLogger(ErrorShower.class);
    public void showErrorMessage(String message) {
        logger.error(message);
        System.out.println(message);
    }
}

