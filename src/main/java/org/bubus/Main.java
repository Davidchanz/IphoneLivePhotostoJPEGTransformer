package org.bubus;

import me.tongfei.progressbar.ProgressBar;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;

public class Main {
    static final Logger logger = Logger.getLogger(Transformer.class);
    public static void main(String[] args) {
        System.out.print("Initializing...");
        Transformer transformer = new Transformer();
        Duration duration;
        if(args.length == 1)
            duration = transformer.transform(args[0]);
        else
            duration = transformer.transform(System.getProperty("user.dir"));

        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
        System.out.println("Transform Time: " + timeInHHMMSS);
        logger.debug("Transform Time: " + timeInHHMMSS);

        System.out.println("Done!");
        logger.debug("Done!");
    }
}