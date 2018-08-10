package com.journeyOS.i007.interfaces;

interface II007Listener {
    void onSceneChanged(long factorId, long state, String packageName);
    void onSceneChangedJson(long factorId, String msg);
}