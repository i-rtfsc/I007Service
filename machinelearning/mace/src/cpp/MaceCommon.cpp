/*
 * Copyright (c) 2022 anqi.huang@outlook.com
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

#include <string>

#include <jni.h>

#include "JniLog.h"
#include "MaceCommon.h"

MaceCommon *MaceCommon::sInstance = NULL;

MaceCommon::MaceCommon() {
    //TODO
}

MaceCommon::~MaceCommon() {
    //TODO
}

MaceCommon *MaceCommon::getInstance() {
    if (sInstance == NULL) {
        sInstance = new MaceCommon();
    }

    return sInstance;
}

DeviceType MaceCommon::parseDeviceType(const string &device) {
    if (device.compare("CPU") == 0) {
        return DeviceType::CPU;
    } else if (device.compare("GPU") == 0) {
        return DeviceType::GPU;
    } else if (device.compare("HEXAGON") == 0) {
        return DeviceType::HEXAGON;
    } else {
        return DeviceType::CPU;
    }
}

string MaceCommon::stringFromJni(JNIEnv *env, jstring &s) {
    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(s, &isCopy);
    string str = string(convertedValue);
    env->ReleaseStringUTFChars(s, convertedValue);
    return str;
}

jstring jni_native_get_mace_version(JNIEnv *env, jclass thiz) {
    const char *mace_version = MaceVersion();
    LOGV("mace version = %s", mace_version);
    return env->NewStringUTF(mace_version);
}


