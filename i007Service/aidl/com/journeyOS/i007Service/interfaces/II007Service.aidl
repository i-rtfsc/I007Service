package com.journeyOS.i007Service.interfaces;

interface II007Service {
    boolean isGame(in String packageName);
    boolean addGame(in String source, in String packageName);
    boolean removeGame(in String source, in String packageName);
}