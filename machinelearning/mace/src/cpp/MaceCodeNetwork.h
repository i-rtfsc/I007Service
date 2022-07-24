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

#ifndef _MACE_CODE_NETWORK_H
#define _MACE_CODE_NETWORK_H

#include "mace/public/mace_engine_factory.h"
#include "mace/public/mace.h"

#include "JniLog.h"

using namespace std;
using namespace mace;

struct ModelInfo {
    string input_name;
    string output_name;
    vector<int64_t> input_shape;
    vector<int64_t> output_shape;
    string version_name;
};

struct MaceContext {
    shared_ptr<GPUContext> gpu_context;
    shared_ptr<MaceEngine> engine;
    string model_name;
    DeviceType device_type = DeviceType::CPU;
    map<string, ModelInfo> model_infos = {
            {"mobilenet_v1",       {"input", "MobilenetV1/Predictions/Reshape_1",
                                           {1, 224, 224, 3}, {1, 1001}, "v1.0.0"}},
            {"mobilenet_v2",       {"input", "MobilenetV2/Predictions/Reshape_1",
                                           {1, 224, 224, 3}, {1, 1001}, "v2.0.0"}},
            {"mobilenet_v1_quantized", {"input", "MobilenetV1/Predictions/Softmax:0",
                                           {1, 224, 224, 3}, {1, 1001}, "v1.0.1"}},
            {"mobilenet_v2_quantized", {"input", "output",
                                           {1, 224, 224, 3}, {1, 1001}, "v2.0.1"}}
    };
};

static struct {
    jclass clazz;
    jobject object;
    jmethodID init;
    jmethodID setModelVersion;
    jmethodID setInputTensorShape;
    jmethodID setOutputTensorShape;
} gNativeMaceClass;

static struct {
    jclass clazz;
    jmethodID entrySet;
    jmethodID put;
} gMapClass;

static struct {
    jclass clazz;
    jmethodID iterator;
} gSetClass;

static struct {
    jclass clazz;
    jmethodID hasNext;
    jmethodID next;
} gIteratorClass;

static struct {
    jclass clazz;
    jmethodID getKey;
    jmethodID getValue;
} gMap_EntryClass;

static struct {
    jclass clazz;
    jmethodID read;
    jmethodID write;
    jmethodID getSize;
} gFloatTensorClass;

static struct {
    jclass clazz;
    jmethodID createFloatTensor;
} gNativeNetworkClass;

static bool gDebug = false;

#endif //_MACE_CODE_NETWORK_H
