package com.journeyOS.i007.interfaces;

import com.journeyOS.i007.interfaces.II007Listener;

interface II007Register {
    boolean registerListener(long factors, II007Listener listener);
    void unregisterListener(II007Listener listener);
}
