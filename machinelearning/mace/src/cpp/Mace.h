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

#ifndef _MACE_H
#define _MACE_H

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
                                           {1, 224, 224, 3}, {1, 1001}, "v1.1.0"}},
            {"mobilenet_v1_quant", {"input", "MobilenetV1/Predictions/Softmax:0",
                                           {1, 224, 224, 3}, {1, 1001}, "v1.0.1"}},
            {"mobilenet_v2_quant", {"input", "output",
                                           {1, 224, 224, 3}, {1, 1001}, "v1.1.1"}}
    };
};

DeviceType ParseDeviceType(const string &device) {
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

MaceContext &GetMaceContext() {
    /**
     * TODO
     * In multi-dlopen process, this step may cause memory leak.
     */
    static auto *mace_context = new MaceContext;
    return *mace_context;
}

#endif //_MACE_H
