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
#include <vector>
#include <numeric>
#include <algorithm>
#include <functional>
#include <map>
#include <memory>

#include <jni.h>

#include "JniLog.h"
#include "MaceCommon.h"
#include "MaceCodeNetwork.h"

static void _init_native_clazz_methods(JNIEnv *env, jobject clazz_obj) {
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

    /**
     * java/util/Map
     */
    gMapClass.clazz = env->FindClass("java/util/Map");
    gMapClass.entrySet = env->GetMethodID(gMapClass.clazz, "entrySet", "()Ljava/util/Set;");
    gMapClass.put = env->GetMethodID(gMapClass.clazz, "put",
                                     "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    /**
     * java/util/Set
     */
    gSetClass.clazz = env->FindClass("java/util/Set");
    gSetClass.iterator = env->GetMethodID(gSetClass.clazz, "iterator", "()Ljava/util/Iterator;");

    /**
     * java/util/Iterator
     */
    gIteratorClass.clazz = env->FindClass("java/util/Iterator");
    gIteratorClass.hasNext = env->GetMethodID(gIteratorClass.clazz, "hasNext", "()Z");
    gIteratorClass.next = env->GetMethodID(gIteratorClass.clazz, "next", "()Ljava/lang/Object;");

    /**
     * java/util/Map$Entry
     */
    gMap_EntryClass.clazz = env->FindClass("java/util/Map$Entry");
    gMap_EntryClass.getKey = env->GetMethodID(gMap_EntryClass.clazz, "getKey",
                                              "()Ljava/lang/Object;");
    gMap_EntryClass.getValue = env->GetMethodID(gMap_EntryClass.clazz, "getValue",
                                                "()Ljava/lang/Object;");

    /**
     * com/journeyOS/mace/core/FloatTensor
     */
    gFloatTensorClass.clazz = env->FindClass("com/journeyOS/mace/core/FloatTensor");
    gFloatTensorClass.read = env->GetMethodID(gFloatTensorClass.clazz, "read", "([FII)I");
    gFloatTensorClass.write = env->GetMethodID(gFloatTensorClass.clazz, "write", "([FII)V");
    gFloatTensorClass.getSize = env->GetMethodID(gFloatTensorClass.clazz, "getSize", "()I");

    /**
     * com/journeyOS/mace/internal/NativeFloatTensor
     */
    gNativeNetworkClass.clazz = env->FindClass("com/journeyOS/mace/internal/NativeFloatTensor");
    gNativeNetworkClass.createFloatTensor = env->GetMethodID(gNativeNetworkClass.clazz, "<init>",
                                                             "([I)V");

    return;
}

jobject jni_native_mace_code_get_model_info(JNIEnv *env, jclass thiz, jstring model_name_str) {
    LOGV("Enter : %s", __func__);

    // parse mace_file_model name
    string model_name = MaceCommon::getInstance()->stringFromJni(env, model_name_str);
    if (gDebug) {
        LOGD("mace_file_model name = %s", model_name.c_str());
    }

    _init_native_clazz_methods(env, thiz);

    MaceContext *maceContext = new MaceContext();
    maceContext->model_name.assign(model_name);

    auto model_info_iter = maceContext->model_infos.find(maceContext->model_name);
    if (model_info_iter == maceContext->model_infos.end()) {
        LOGE("Invalid mace_file_model name: %s", maceContext->model_name.c_str());
        return nullptr;
    }

    /**
     * 获取模型版本号
     */
    string version_name = model_info_iter->second.version_name;
    if (gDebug) {
        LOGD("%s(), version_name: %s", __func__, version_name.c_str());
    }
    /**
     * 调用 setModelVersion 方法
     */
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setModelVersion,
                        env->NewStringUTF(version_name.c_str()));
    if (gDebug) {
        LOGD("%s(), call java setModelVersion() finish ", __func__);
    }

    /**
     * 获取模型input shape
     */
    string input_tensor_name = model_info_iter->second.input_name;
    const vector<int64_t> &input_shape = model_info_iter->second.input_shape;
    int input_size = input_shape.size();
    jintArray inputArray = env->NewIntArray(input_size);
    auto int_shape = new jint[input_size];
    for (int i = 0; i < input_size; ++i) {
        int_shape[i] = input_shape[i];
    }
    env->SetIntArrayRegion(inputArray, 0, input_size, int_shape);
    /**
     * 调用 setInputTensorShape 方法
     */
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setInputTensorShape,
                        env->NewStringUTF(input_tensor_name.c_str()), inputArray);
    if (gDebug) {
        LOGD("%s(), call java setInputTensorShape() finish ", __func__);
    }

    /**
     * 获取模型output shape
     */
    string output_tensor_name = model_info_iter->second.output_name;
    const vector<int64_t> &output_shape = model_info_iter->second.output_shape;
    int output_size = output_shape.size();
    jintArray outputArray = env->NewIntArray(output_size);
    auto out_shape = new jint[output_size];
    for (int i = 0; i < input_size; ++i) {
        out_shape[i] = output_shape[i];
    }
    env->SetIntArrayRegion(outputArray, 0, output_size, out_shape);
    /**
     * 调用 setOutputTensorShape 方法
     */
    env->CallVoidMethod(gNativeMaceClass.object, gNativeMaceClass.setOutputTensorShape,
                        env->NewStringUTF(output_tensor_name.c_str()), outputArray);
    if (gDebug) {
        LOGD("%s(), call java setOutputTensorShape() finish ", __func__);
    }

    delete maceContext;

    return gNativeMaceClass.object;
}

jlong jni_native_mace_code_create_network_engine(JNIEnv *env, jclass thiz,
                                                 jstring model_name_str,
                                                 jstring target_runtime,
                                                 jstring storage_path,
                                                 jint opencl_cache_reuse_policy,
                                                 jint omp_num_threads,
                                                 jint cpu_affinity_policy,
                                                 jint gpu_perf_hint,
                                                 jint gpu_priority_hint,
                                                 jboolean debug) {
    LOGV("%s(), start", __func__);
    gDebug = debug;
    LOGI("debug log enable = %d", gDebug);

    /**
     * storage path
     * DO NOT USE tmp directory.
     * Please use APP's own directory and make sure the directory exists.
     */
    string _storage_file_path = MaceCommon::getInstance()->stringFromJni(env, storage_path);
    if (gDebug) {
        LOGD("storage path = %s", _storage_file_path.c_str());
    }

    /**
     * create MaceContext
     */
    MaceContext *maceContext = new MaceContext;

    /**
     * SetStoragePath will be replaced by SetOpenCLCacheFullPath in the future
     */
    if (strlen(_storage_file_path.c_str()) > 0) {
        maceContext->gpu_context = GPUContextBuilder()
                .SetStoragePath(_storage_file_path)
//                mace tag v1.0.2 无此接口
//                .SetOpenCLCacheReusePolicy(
//                        static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
                .Finalize();
    } else {
        maceContext->gpu_context = GPUContextBuilder()
//                mace tag v1.0.2 无此接口
//                .SetOpenCLCacheReusePolicy(
//                        static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
                .Finalize();
    }

    /**
     * get device(runtime)
     */
    string _target_runtime = MaceCommon::getInstance()->stringFromJni(env, target_runtime);
    if (gDebug) {
        LOGD("runtime = %s", _target_runtime.c_str());
    }
    maceContext->device_type = MaceCommon::getInstance()->parseDeviceType(_target_runtime);


    /**
     * create MaceEngineConfig
     */
    MaceStatus status;
    MaceEngineConfig config(maceContext->device_type);
    status = config.SetCPUThreadPolicy(omp_num_threads,
                                       static_cast<CPUAffinityPolicy>(cpu_affinity_policy));
    if (gDebug) {
        LOGD("SetCPUThreadPolicy status = %d", status.code());
    }
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


    /**
     * parse mace_file_model name
     */
    string _model_name = MaceCommon::getInstance()->stringFromJni(env, model_name_str);
    if (gDebug) {
        LOGD("model name = %s", _model_name.c_str());
    }

    maceContext->model_name.assign(_model_name);

    /**
     * load mace_file_model input and output name
     */
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

    if (gDebug) {
        LOGD("Leave : %s with maceContext = %ld : 0x%lx", __func__, (jlong) (maceContext),
             (jlong) (maceContext));
    }
    return (jlong) (maceContext);
}

jfloatArray jni_native_mace_code_execute_float(JNIEnv *env, jclass thiz,
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

    /**
     * load input
     */
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
    /**
     * construct input
     */
    auto buffer_in = shared_ptr<float>(new float[input_size],
                                       default_delete<float[]>());
    copy_n(input_data_ptr, input_size, buffer_in.get());
    env->ReleaseFloatArrayElements(input_data, input_data_ptr, 0);
    inputs[input_name] = MaceTensor(input_shape, buffer_in);

    /**
     * construct output
     */
    auto buffer_out = shared_ptr<float>(new float[output_size],
                                        default_delete<float[]>());
    outputs[output_name] = MaceTensor(output_shape, buffer_out);

    /**
     * run mace_file_model
     */
    maceContext->engine->Run(inputs, &outputs);

    /**
     * transform output
     * allocate
     */
    jfloatArray jOutputData = env->NewFloatArray(output_size);
    if (jOutputData == nullptr) {
        return nullptr;
    }

    /**
     * copy
     */
    env->SetFloatArrayRegion(jOutputData, 0, output_size, outputs[output_name].data().get());

    return jOutputData;
}

jboolean jni_native_mace_code_execute(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context,
                                      jobject input_tensors,
                                      jobject output_tensors) {
    LOGV("%s() with native_mace_context = %ld : 0x%lx", __func__,
         (jlong) (native_mace_context),
         (jlong) (native_mace_context));

    _init_native_clazz_methods(env, thiz);

    MaceContext *maceContext = (MaceContext *) native_mace_context;

    //  prepare input and output
    auto model_info_iter = maceContext->model_infos.find(maceContext->model_name);
    if (model_info_iter == maceContext->model_infos.end()) {
        LOGE("Invalid mace_file_model name: %s", maceContext->model_name.c_str());
        return false;
    }

    const ModelInfo &model_info = model_info_iter->second;

    const string &input_name = model_info.input_name;
    const string &output_name = model_info.output_name;

    const vector<int64_t> &input_shape = model_info.input_shape;
    const vector<int64_t> &output_shape = model_info.output_shape;

    const int64_t input_size = accumulate(input_shape.begin(), input_shape.end(), 1,
                                          multiplies<int64_t>());
    const int64_t output_size = accumulate(output_shape.begin(), output_shape.end(), 1,
                                           multiplies<int64_t>());


    map<string, MaceTensor> inputs;
    map<string, MaceTensor> outputs;

    /**
     * for input tensor
     */
    jobject inputTensorsKeyObj = env->CallObjectMethod(input_tensors, gMapClass.entrySet);
    jobject inputTensorsKeyIteratorObj = env->CallObjectMethod(inputTensorsKeyObj,
                                                               gSetClass.iterator);
    while (env->CallBooleanMethod(inputTensorsKeyIteratorObj, gIteratorClass.hasNext)) {
        jobject entryObj = env->CallObjectMethod(inputTensorsKeyIteratorObj, gIteratorClass.next);
        jstring inputTensorName = (jstring) env->CallObjectMethod(entryObj, gMap_EntryClass.getKey);
        jobject inputTensor = (jobject) env->CallObjectMethod(entryObj, gMap_EntryClass.getValue);

        const char *inputTensorName_ptr = env->GetStringUTFChars(inputTensorName, nullptr);

        auto tensorShape = input_name.c_str();
        env->ReleaseStringUTFChars(inputTensorName, inputTensorName_ptr);

        /**
         * tensor shape
         */
        const int tensor_size = input_size;

        /**
         * check input name and size
         */
        jint input_size = env->CallIntMethod(inputTensor, gFloatTensorClass.getSize);
        if (input_size != tensor_size) {
            LOGE("input_size(%d) not equal as extern tensor_size(%d)", input_size, tensor_size);
            return false;
        }
        LOGV("execute input tensor name : %s , tensor_size = %d, input_size = %d", tensorShape,
             tensor_size, input_size);

        jfloatArray read_buffer = (jfloatArray) env->NewFloatArray(input_size);
        jint read_size = env->CallIntMethod(inputTensor, gFloatTensorClass.read, read_buffer, 0,
                                            input_size);
        jfloat *read_native_buffer = env->GetFloatArrayElements(read_buffer, 0);
        auto input_tensor_data = shared_ptr<float>(new float[input_size],
                                                   default_delete<float[]>());
        copy_n(read_native_buffer, input_size, input_tensor_data.get());
        env->ReleaseFloatArrayElements(read_buffer, read_native_buffer, 0);
        env->DeleteLocalRef(read_buffer);

        LOGV("read_size = %d", read_size);
        inputs[tensorShape] = MaceTensor(input_shape, input_tensor_data);
    }

    /**
     * for output tensor
     */

    auto buffer_out = shared_ptr<float>(new float[output_size], default_delete<float[]>());
    /**
     * check input name and size
     */
    outputs[output_name.c_str()] = MaceTensor(output_shape, buffer_out);

    /**
     * inference
     */
    MaceStatus status = maceContext->engine->Run(inputs, &outputs);
    LOGV("inference status = %d", status.code());

    if (status == MaceStatus::MACE_SUCCESS) {
        for (auto &kv: outputs) {
            auto java_output_tensor_name = kv.first;
            auto java_output_tensor_shape = kv.second.shape();
            auto java_output_tensor_data = kv.second.data().get();

            /**
             * construct shape array
             */
            jintArray shape = (jintArray) env->NewIntArray(java_output_tensor_shape.size());
            auto int_shape = new jint[java_output_tensor_shape.size()];
            for (int i = 0; i < java_output_tensor_shape.size(); i++) {
                int_shape[i] = java_output_tensor_shape.data()[i];
            }
            env->SetIntArrayRegion(shape, 0, java_output_tensor_shape.size(), int_shape);

            /**
             * create the FloatTensor object in java
             */
            jobject java_float_tensor = env->NewObject(gNativeNetworkClass.clazz,
                                                       gNativeNetworkClass.createFloatTensor,
                                                       shape);

            delete[] int_shape;
            env->DeleteLocalRef(shape);

            /**
             * construct data array
             */
            int output_size = env->CallIntMethod(java_float_tensor, gFloatTensorClass.getSize);
            jfloatArray data = (jfloatArray) env->NewFloatArray(output_size);
            auto float_data = new jfloat[output_size];
            for (int i = 0; i < output_size; i++) {
                float_data[i] = java_output_tensor_data[i];
            }
            env->SetFloatArrayRegion(data, 0, output_size, float_data);
            delete[] float_data;

            env->CallVoidMethod(java_float_tensor, gFloatTensorClass.write, data, 0, output_size);
            env->DeleteLocalRef(data);

            /**
             * add to outputs map in java
             */
            jstring java_string = env->NewStringUTF(java_output_tensor_name.c_str());
            env->CallObjectMethod(output_tensors, gMapClass.put, java_string, java_float_tensor);
            env->DeleteLocalRef(java_string);
        }
    }
    LOGV("Leave : %s with maceContext = %ld : 0x%lx", __func__, (jlong) (maceContext),
         (jlong) (maceContext));
    return status == MaceStatus::MACE_SUCCESS;
}

jboolean jni_native_mace_code_release(JNIEnv *env, jobject thiz, jlong native_mace_context) {
    if (gDebug) {
        LOGD("%s() with native_mace_context = %ld : 0x%lx", __func__, (jlong) (native_mace_context),
             (jlong) (native_mace_context));
    }

    MaceContext *maceContext = (MaceContext *) native_mace_context;
    delete maceContext;

    return true;
}