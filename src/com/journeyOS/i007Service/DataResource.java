/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.i007Service;

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
