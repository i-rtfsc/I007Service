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

#include <android/log.h>
#include <jni.h>

#include <algorithm>
#include <functional>
#include <map>
#include <memory>
#include <string>
#include <vector>
#include <numeric>

#include "JniLog.h"
#include "Mace.h"

jstring jni_native_get_mace_version(JNIEnv *env, jclass thiz) {
    const char *mace_version = MaceVersion();
    LOGI("mace version = %s", mace_version);
    return env->NewStringUTF(mace_version);
}

jstring jni_native_get_model_version(JNIEnv *env, jclass thiz, jstring model_name_str) {
    MaceContext &mace_context = GetMaceContext();

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return nullptr;
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return nullptr;
    }

    string version_name = model_info_iter->second.version_name;
    return env->NewStringUTF(version_name.c_str());
}

jstring jni_native_get_input_tensor_name(JNIEnv *env, jclass thiz, jstring model_name_str) {
    MaceContext &mace_context = GetMaceContext();

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return nullptr;
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return nullptr;
    }

    string input_tensor_name = model_info_iter->second.input_name;

    return env->NewStringUTF(input_tensor_name.c_str());
}

jintArray jni_native_get_input_tensor_shape(JNIEnv *env, jclass thiz, jstring model_name_str) {
    MaceContext &mace_context = GetMaceContext();

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return nullptr;
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return nullptr;
    }

    const vector<int64_t> &input_shape = model_info_iter->second.input_shape;

    int input_size = input_shape.size();

    jintArray inputArray = env->NewIntArray(input_size);
    jint *input_buf = new jint[input_size];
    for (int i = 0; i < input_size; ++i) {
        *(input_buf + i) = input_shape[i];
    }
    env->SetIntArrayRegion(inputArray, 0, input_size, input_buf);

    return inputArray;
}

jstring jni_native_get_output_tensor_name(JNIEnv *env, jclass thiz, jstring model_name_str) {
    MaceContext &mace_context = GetMaceContext();

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return env->NewStringUTF("");
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return env->NewStringUTF("");
    }

    string output_tensor_name = model_info_iter->second.output_name;

    return env->NewStringUTF(output_tensor_name.c_str());
}

jintArray jni_native_get_output_tensor_shape(JNIEnv *env, jclass thiz, jstring model_name_str) {
    MaceContext &mace_context = GetMaceContext();

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return nullptr;
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return nullptr;
    }

    const vector<int64_t> &output_shape = model_info_iter->second.output_shape;

    int input_size = output_shape.size();

    jintArray outputArray = env->NewIntArray(input_size);
    jint *input_buf = new jint[input_size];
    for (int i = 0; i < input_size; ++i) {
        *(input_buf + i) = output_shape[i];
    }
    env->SetIntArrayRegion(outputArray, 0, input_size, input_buf);

    return outputArray;
}

jint jni_native_create_network(JNIEnv *env, jclass thiz, jstring storage_path,
                               jstring opencl_cache_full_path, jint opencl_cache_reuse_policy) {
    MaceContext &mace_context = GetMaceContext();
    // DO NOT USE tmp directory.
    // Please use APP's own directory and make sure the directory exists.
    const char *storage_path_ptr = env->GetStringUTFChars(storage_path, nullptr);
    if (storage_path_ptr == nullptr) {
        return JNI_ERR;
    }

    const string storage_file_path(storage_path_ptr);
    env->ReleaseStringUTFChars(storage_path, storage_path_ptr);

    const char *opencl_cache_full_path_ptr =
            env->GetStringUTFChars(opencl_cache_full_path, nullptr);
    if (opencl_cache_full_path_ptr == nullptr) {
        return JNI_ERR;
    }

    const string str_opencl_cache_full_path(opencl_cache_full_path_ptr);
    env->ReleaseStringUTFChars(opencl_cache_full_path,
                               opencl_cache_full_path_ptr);

    // SetStoragePath will be replaced by SetOpenCLCacheFullPath in the future
    mace_context.gpu_context = GPUContextBuilder()
            .SetStoragePath(storage_file_path)
            .SetOpenCLCacheFullPath(str_opencl_cache_full_path)
            .SetOpenCLCacheReusePolicy(
                    static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
            .Finalize();

    return JNI_OK;
}

jint jni_native_create_engine(JNIEnv *env, jclass thiz,
                              jstring model_name_str, jstring device,
                              jint omp_num_threads, jint cpu_affinity_policy,
                              jint gpu_perf_hint, jint gpu_priority_hint) {
    MaceContext &mace_context = GetMaceContext();

    // get device
    const char *device_ptr = env->GetStringUTFChars(device, nullptr);
    if (device_ptr == nullptr) {
        return JNI_ERR;
    }

    mace_context.device_type = ParseDeviceType(device_ptr);
    env->ReleaseStringUTFChars(device, device_ptr);

    // create MaceEngineConfig
    MaceStatus status;
    MaceEngineConfig config(mace_context.device_type);
    status = config.SetCPUThreadPolicy(
            omp_num_threads,
            static_cast<CPUAffinityPolicy>(cpu_affinity_policy));
    if (status != MaceStatus::MACE_SUCCESS) {
        LOGE("openmp result: %s, threads: %d, cpu: %d",
             status.information().c_str(), omp_num_threads,
             cpu_affinity_policy);
    }
    if (mace_context.device_type == DeviceType::GPU) {
        config.SetGPUContext(mace_context.gpu_context);
        config.SetGPUHints(
                static_cast<GPUPerfHint>(gpu_perf_hint),
                static_cast<GPUPriorityHint>(gpu_priority_hint));
        LOGI("gpu perf: %d, priority: %d",
             gpu_perf_hint, gpu_priority_hint);
    }

    LOGI("device: %d", mace_context.device_type);

    //  parse model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return JNI_ERR;
    }

    mace_context.model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    //  load model input and output name
    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return JNI_ERR;
    }
    vector<string> input_names = {model_info_iter->second.input_name};
    vector<string> output_names = {model_info_iter->second.output_name};

    MaceStatus create_engine_status =
            CreateMaceEngineFromCode(mace_context.model_name,
                                     nullptr,
                                     0,
                                     input_names,
                                     output_names,
                                     config,
                                     &mace_context.engine);

    LOGI("create result: %s",
         create_engine_status.information().c_str());

    return create_engine_status == MaceStatus::MACE_SUCCESS ?
           JNI_OK : JNI_ERR;
}

jfloatArray jni_native_execute(JNIEnv *env, jclass thiz, jfloatArray input_data) {
    MaceContext &mace_context = GetMaceContext();
    //  prepare input and output
    auto model_info_iter = mace_context.model_infos.find(mace_context.model_name);
    if (model_info_iter == mace_context.model_infos.end()) {
        LOGE("Invalid model name: %s", mace_context.model_name.c_str());
        return nullptr;
    }

    const ModelInfo &model_info = model_info_iter->second;
    const string &input_name = model_info.input_name;
    const string &output_name = model_info.output_name;
    const vector<int64_t> &input_shape = model_info.input_shape;
    const vector<int64_t> &output_shape = model_info.output_shape;
    const int64_t input_size =
            accumulate(input_shape.begin(), input_shape.end(), 1,
                       multiplies<int64_t>());
    const int64_t output_size =
            accumulate(output_shape.begin(), output_shape.end(), 1,
                       multiplies<int64_t>());

    //  load input
    jfloat *input_data_ptr = env->GetFloatArrayElements(input_data, nullptr);
    if (input_data_ptr == nullptr) {
        return nullptr;
    }

    jsize length = env->GetArrayLength(input_data);
    if (length != input_size) {
        return nullptr;
    }

    map<string, MaceTensor> inputs;
    map<string, MaceTensor> outputs;
    // construct input
    auto buffer_in = shared_ptr<float>(new float[input_size],
                                       default_delete<float[]>());
    copy_n(input_data_ptr, input_size, buffer_in.get());
    env->ReleaseFloatArrayElements(input_data, input_data_ptr, 0);
    inputs[input_name] = MaceTensor(input_shape, buffer_in);

    // construct output
    auto buffer_out = shared_ptr<float>(new float[output_size],
                                        default_delete<float[]>());
    outputs[output_name] = MaceTensor(output_shape, buffer_out);

    // run model
    mace_context.engine->Run(inputs, &outputs);

    // transform output
    jfloatArray jOutputData = env->NewFloatArray(output_size);  // allocate
    if (jOutputData == nullptr) {
        return nullptr;
    }

    env->SetFloatArrayRegion(jOutputData, 0, output_size,
                             outputs[output_name].data().get());  // copy

    return jOutputData;
}