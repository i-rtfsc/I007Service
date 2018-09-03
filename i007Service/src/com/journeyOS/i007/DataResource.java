package com.journeyOS.i007;

public class DataResource {
    public enum FACTORY {//factoryId
        APP(1),
        LCD(2),
        NET(3),
        HEADSET(4),
        BATTERY(5);

        private int value;

        private FACTORY(int value) {
            this.value = value;
        }
    }

    public enum APP {
        DEFAULT,
        ALBUM,
        BROWSER,
        GAME,
        IM,
        MUSIC,
        NEWS,
        READER,
        VIDEO
    }

    public enum NetWork {
        DISCONNECTED(-2),
        UNKNOWN(-1),
        WIFI(1),
        NET4G(2),
        NET3G(3),
        NET2G(4);

        public int value;

        NetWork(int value) {
            this.value = value;
        }
    }
}
