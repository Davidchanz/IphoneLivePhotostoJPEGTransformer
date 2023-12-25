package org.bubus;

import me.tongfei.progressbar.ProgressBar;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;

public class Main {
    static final Logger logger = Logger.getLogger(Transformer.class);
    public static void main(String[] args) {
        Transformer transformer = new Transformer();
        if(args.length == 1)
            transformer.transform(args[0]);
        else
            transformer.transform(System.getProperty("user.dir"));
    }
}