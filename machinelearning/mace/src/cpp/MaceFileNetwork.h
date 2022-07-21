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

#ifndef _MACE_FILE_NETWORK_H
#define _MACE_FILE_NETWORK_H

#include <string>
#include <vector>
#include "mace/public/mace.h"

using namespace std;
using namespace mace;

struct ModelInfo {
    map<string, vector<int64_t>> input_tensors;
    map<string, vector<int64_t>> output_tensors;
    string model_graph_path;
    string model_data_path;
};

struct MaceContext {
    shared_ptr<GPUContext> gpu_context;
    shared_ptr<MaceEngine> engine;
    string model_name;
    DeviceType device_type = DeviceType::CPU;
    ModelInfo model_infos;
};


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

#endif //_MACE_FILE_NETWORK_H
