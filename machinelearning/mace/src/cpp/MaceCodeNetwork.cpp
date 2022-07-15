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
#include "MaceCodeNetwork.h"

static DeviceType ParseDeviceType(const string &device) {
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

static void init_native_clazz_methods(JNIEnv *env, jobject clazz_obj) {
    /**
     * com/journeyOS/mace/internal/NativeMace
     */
    gNativeMaceClass.clazz = env->FindClass("com/journeyOS/mace/internal/NativeMace");

    /**
     * 获取构造函数
     */
    gNativeMaceClass.init = env->GetMethodID(gNativeMaceClass.clazz, "<init>", "()V");

    gNativeMaceClass.object = env->NewObject(gNativeMaceClass.clazz, gNativeMaceClass.init);

    gNativeMaceClass.setModelVersion = env->GetMethodID(gNativeMaceClass.clazz,
                                                        "setModelVersion",
                                                        "(Ljava/lang/String;)V");

    /**
     * 获取 setInputTensorShape 函数
     */
    gNativeMaceClass.setInputTensorShape = env->GetMethodID(gNativeMaceClass.clazz,
                                                            "setInputTensorShape",
                                                            "(Ljava/lang/String;[I)V");
    /**
     * 获取 setOutputTensorShape 函数
     */
    gNativeMaceClass.setOutputTensorShape = env->GetMethodID(gNativeMaceClass.clazz,
                                                             "setOutputTensorShape",
                                                             "(Ljava/lang/String;[I)V");
    return;
}

jobject jni_native_get_mace_model_info(JNIEnv *env, jclass thiz, jstring model_name_str) {
    LOGV("Enter : %s", __func__);
    init_native_clazz_methods(env, thiz);

    const char *mace_version = MaceVersion();
    LOGI("mace version = %s", mace_version);

    MaceContext *maceContext = new MaceContext();

    //  parse mace_file_model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return nullptr;
    }

    maceContext->model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    auto model_info_iter = maceContext->model_infos.find(maceContext->model_name);
    if (model_info_iter == maceContext->model_infos.end()) {
        LOGE("Invalid mace_file_model name: %s", maceContext->model_name.c_str());
        return nullptr;
    }

    string version_name = model_info_iter->second.version_name;
    if (DEBUG) {
        LOGD("%s(), version_name: %s", __func__, version_name.c_str());
    }

    //调用 setModelVersion 方法
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setModelVersion,
                        env->NewStringUTF(version_name.c_str()));

//    jobject native_mace_obj = env->NewObject(gNativeMaceClass.clazz, gNativeMaceClass.init,
//                                             env->NewStringUTF(mace_version),
//                                             env->NewStringUTF(version_name.c_str()));
    if (DEBUG) {
        LOGD("%s(), call java init finish ", __func__);
    }

    string input_tensor_name = model_info_iter->second.input_name;
    const vector<int64_t> &input_shape = model_info_iter->second.input_shape;
    int input_size = input_shape.size();
    jintArray inputArray = env->NewIntArray(input_size);
    auto int_shape = new jint[input_size];
    for (int i = 0; i < input_size; ++i) {
        int_shape[i] = input_shape[i];
    }
    env->SetIntArrayRegion(inputArray, 0, input_size, int_shape);
    //调用 setInputTensorShape 方法
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setInputTensorShape,
                        env->NewStringUTF(input_tensor_name.c_str()), inputArray);


    string output_tensor_name = model_info_iter->second.output_name;
    const vector<int64_t> &output_shape = model_info_iter->second.output_shape;
    int output_size = output_shape.size();
    jintArray outputArray = env->NewIntArray(output_size);
    auto out_shape = new jint[output_size];
    for (int i = 0; i < input_size; ++i) {
        out_shape[i] = output_shape[i];
    }
    env->SetIntArrayRegion(outputArray, 0, output_size, out_shape);
    //调用 setInputTensorShape 方法
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setOutputTensorShape,
                        env->NewStringUTF(output_tensor_name.c_str()), outputArray);

    delete maceContext;

    return gNativeMaceClass.object;
}

jstring jni_native_get_mace_version(JNIEnv *env, jclass thiz) {
    const char *mace_version = MaceVersion();
    LOGI("mace version = %s", mace_version);
    return env->NewStringUTF(mace_version);
}

jlong jni_native_mace_code_create_network_engine(JNIEnv *env, jclass thiz,
                                                 jstring model_name_str,
                                                 jstring target_runtime,
                                                 jstring storage_path,
                                                 jstring opencl_cache_full_path,
                                                 jint opencl_cache_reuse_policy,
                                                 jint omp_num_threads,
                                                 jint cpu_affinity_policy,
                                                 jint gpu_perf_hint,
                                                 jint gpu_priority_hint) {

    // DO NOT USE tmp directory.
    // Please use APP's own directory and make sure the directory exists.
    const char *storage_path_ptr = env->GetStringUTFChars(storage_path, nullptr);
    if (storage_path_ptr == nullptr) {
        return JNI_ERR;
    }

    const string storage_file_path(storage_path_ptr);
    env->ReleaseStringUTFChars(storage_path, storage_path_ptr);

    const char *opencl_cache_full_path_ptr = env->GetStringUTFChars(opencl_cache_full_path,
                                                                    nullptr);
    if (opencl_cache_full_path_ptr == nullptr) {
        return JNI_ERR;
    }

    const string str_opencl_cache_full_path(opencl_cache_full_path_ptr);
    env->ReleaseStringUTFChars(opencl_cache_full_path, opencl_cache_full_path_ptr);

    // create MaceContext
    MaceContext *maceContext = new MaceContext;

    // SetStoragePath will be replaced by SetOpenCLCacheFullPath in the future
    maceContext->gpu_context = GPUContextBuilder()
            .SetStoragePath(storage_file_path)
            .SetOpenCLCacheFullPath(str_opencl_cache_full_path)
            .SetOpenCLCacheReusePolicy(
                    static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
            .Finalize();

    // get device
    const char *target_runtime_ptr = env->GetStringUTFChars(target_runtime, nullptr);
    if (target_runtime_ptr == nullptr) {
        return JNI_ERR;
    }

    maceContext->device_type = ParseDeviceType(target_runtime_ptr);
    env->ReleaseStringUTFChars(target_runtime, target_runtime_ptr);

    // create MaceEngineConfig
    MaceStatus status;
    MaceEngineConfig config(maceContext->device_type);
    status = config.SetCPUThreadPolicy(omp_num_threads,
                                       static_cast<CPUAffinityPolicy>(cpu_affinity_policy));
    if (status != MaceStatus::MACE_SUCCESS) {
        LOGE("openmp result: %s, threads: %d, cpu: %d", status.information().c_str(),
             omp_num_threads, cpu_affinity_policy);
    }
    if (maceContext->device_type == DeviceType::GPU) {
        config.SetGPUContext(maceContext->gpu_context);
        config.SetGPUHints(static_cast<GPUPerfHint>(gpu_perf_hint),
                           static_cast<GPUPriorityHint>(gpu_priority_hint));
        LOGI("gpu perf: %d, priority: %d", gpu_perf_hint, gpu_priority_hint);
    }

    LOGI("device: %d", maceContext->device_type);

    // parse mace_file_model name
    const char *model_name_ptr = env->GetStringUTFChars(model_name_str, nullptr);
    if (model_name_ptr == nullptr) {
        return JNI_ERR;
    }

    maceContext->model_name.assign(model_name_ptr);
    env->ReleaseStringUTFChars(model_name_str, model_name_ptr);

    // load mace_file_model input and output name
    auto model_info_iter = maceContext->model_infos.find(maceContext->model_name);
    if (model_info_iter == maceContext->model_infos.end()) {
        LOGE("Invalid mace_file_model name: %s", maceContext->model_name.c_str());
        return JNI_ERR;
    }

    vector<string> input_names = {model_info_iter->second.input_name};
    vector<string> output_names = {model_info_iter->second.output_name};

    MaceStatus create_engine_status = CreateMaceEngineFromCode(maceContext->model_name,
                                                               nullptr,
                                                               0,
                                                               input_names,
                                                               output_names,
                                                               config,
                                                               &maceContext->engine);

    LOGI("create result, status = %d, msg = %s", create_engine_status.code(),
         create_engine_status.information().c_str());

    LOGV("Leave : %s with maceContext = %ld : 0x%lx", __func__, (jlong) (maceContext),
         (jlong) (maceContext));
    return (jlong) (maceContext);
}

jfloatArray jni_native_mace_code_execute(JNIEnv *env, jclass thiz,
                                         jlong native_mace_context,
                                         jfloatArray input_data) {

    LOGV("%s() start, native_mace_context = %ld : 0x%lx", __func__,
         (jlong) (native_mace_context),
         (jlong) (native_mace_context));
    MaceContext *maceContext = (MaceContext *) native_mace_context;

    //  prepare input and output
    auto model_info_iter = maceContext->model_infos.find(maceContext->model_name);
    if (model_info_iter == maceContext->model_infos.end()) {
        LOGE("Invalid mace_file_model name: %s", maceContext->model_name.c_str());
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

    // run mace_file_model
    maceContext->engine->Run(inputs, &outputs);

    // transform output
    jfloatArray jOutputData = env->NewFloatArray(output_size);  // allocate
    if (jOutputData == nullptr) {
        return nullptr;
    }

    env->SetFloatArrayRegion(jOutputData, 0, output_size,
                             outputs[output_name].data().get());  // copy

    return jOutputData;
}


jboolean jni_native_mace_code_release(JNIEnv *env, jobject thiz, jlong native_mace_context) {
//    LOGV("%s() with native_mace_context = %ld : 0x%lx", __func__,
//         (jlong) (native_mace_context),
//         (jlong) (native_mace_context));
//    MaceContext *maceContext = (MaceContext *) native_mace_context;
//    delete maceContext;

    return true;
}