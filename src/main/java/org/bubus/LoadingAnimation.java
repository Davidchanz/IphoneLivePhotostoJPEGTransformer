package org.bubus;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LoadingAnimation implements Runnable{
    static final Logger logger = Logger.getLogger(LoadingAnimation.class);

    private Character[] animations = {'|', '/', 196, '\\'};
    @Override
    public void run() {
        int i = 0;
        while (!Thread.interrupted()) {
            System.out.print("\b");
            System.out.print(animations[i++]);
            if(i == animations.length)
                i = 0;

        }
        logger.debug("Animation Thread Interrupted");
    }
}
