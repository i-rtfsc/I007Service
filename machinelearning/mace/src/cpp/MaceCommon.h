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


#ifndef _MACE_COMMON_H
#define _MACE_COMMON_H

#include <string>
#include <vector>
#include "mace/public/mace.h"

using namespace std;
using namespace mace;

class MaceCommon {
public:
    MaceCommon();

    ~MaceCommon();

    static MaceCommon *getInstance();

    DeviceType parseDeviceType(const string &device);

private:
    static MaceCommon *sInstance;
};

#endif //_MACE_COMMON_H
