package com.ee.portfolio.utils;

import de.greenrobot.event.EventBus;

/**
 *
 */

public class PortfolioBus {

    private static EventBus instance;

    static {
        instance = null;
    }

    private PortfolioBus() {
        instance = new EventBus();
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
}

