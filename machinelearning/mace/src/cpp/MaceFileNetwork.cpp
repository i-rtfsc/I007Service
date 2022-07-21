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

#include <jni.h>

#include "mace/port/file_system.h"
#include "mace/port/env.h"
#include "mace/utils/memory.h"

#include "JniLog.h"
#include "MaceCommon.h"
#include "MaceFileNetwork.h"

static void _init_native_clazz_methods(JNIEnv *env, jobject clazz_obj) {
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

static MaceStatus _init_mace_engine(MaceContext *maceContext,
                                    MaceEngineConfig &config,
                                    int omp_num_threads,
                                    int cpu_affinity_policy) {
    MaceStatus status;

    /**
     * load the mace_file_model graph file
     */
    unique_ptr<port::ReadOnlyMemoryRegion> model_graph_data = make_unique<port::ReadOnlyBufferMemoryRegion>();
    if (maceContext->model_infos.model_graph_path != "") {
        auto fs = GetFileSystem();
        status = fs->NewReadOnlyMemoryRegionFromFile(
                maceContext->model_infos.model_graph_path.c_str(), &model_graph_data);
        if (gDebug) {
            LOGD("%s(), load the mace_file_model graph file, mace status = %d", __func__,
                 status.code());
        }
        if (status != MaceStatus::MACE_SUCCESS) {
            LOGE("could not read mace_file_model graph file: %s with error msg: %s",
                 maceContext->model_infos.model_graph_path.c_str(), status.information().c_str());
        }
    }

    /**
     * load the mace_file_model data file
     */
    unique_ptr<port::ReadOnlyMemoryRegion> model_weights_data = make_unique<port::ReadOnlyBufferMemoryRegion>();
    if (maceContext->model_infos.model_data_path != "") {
        auto fs = GetFileSystem();
        status = fs->NewReadOnlyMemoryRegionFromFile(
                maceContext->model_infos.model_data_path.c_str(),
                &model_weights_data);
        if (gDebug) {
            LOGD("%s(), load the mace_file_model data file, mace status = %d", __func__,
                 status.code());
        }
        if (status != MaceStatus::MACE_SUCCESS) {
            LOGE("could not read mace_file_model data file: %s with error msg: %s",
                 maceContext->model_infos.model_data_path.c_str(), status.information().c_str());
        }
    }

    CPUAffinityPolicy policy = static_cast<CPUAffinityPolicy>(cpu_affinity_policy);
    status = config.SetCPUThreadPolicy(omp_num_threads, policy);
    if (gDebug) {
        LOGD("%s(), set cpu thread policy, mace status = %d", __func__,
             status.code());
    }
    if (status != MaceStatus::MACE_SUCCESS) {
        LOGE("%s(), set cpu thread policy fail threads: %d, cpu: %d", __func__, omp_num_threads,
             policy);
    }

    vector<string> input_nodes;
    for (auto &key: maceContext->model_infos.input_tensors) {
        input_nodes.push_back(key.first.c_str());
        if (gDebug) {
            LOGD("%s(), input tensor name = %s", __func__, key.first.c_str());
        }
    }

    vector<string> output_nodes;
    for (auto &key: maceContext->model_infos.output_tensors) {
        output_nodes.push_back(key.first.c_str());
        if (gDebug) {
            LOGD("%s(), output tensor name = %s", __func__, key.first.c_str());
        }
    }

    status = CreateMaceEngineFromProto(
            reinterpret_cast<const unsigned char *>(model_graph_data->data()),
            model_graph_data->length(),
            reinterpret_cast<const unsigned char *>(model_weights_data->data()),
            model_weights_data->length(),
            input_nodes,
            output_nodes,
            config,
            &(maceContext->engine));
    if (gDebug) {
        LOGD("%s(), CreateMaceEngineFromProto, mace status = %d", __func__,
             status.code());
    }
    if (status != MaceStatus::MACE_SUCCESS) {
        /**
         * fall back to other strategy.
         */
        LOGE("CreateMaceEngineFromProto fail");
    }
    return status;
}

jlong jni_native_mace_file_create_network_engine(JNIEnv *env, jobject thiz,
                                                 jstring model_name,
                                                 jstring target_runtime,
                                                 jstring model_graph_file_path,
                                                 jstring model_data_file_path,
                                                 jstring storage_directory,
                                                 jint opencl_cache_reuse_policy,
                                                 jint omp_num_threads,
                                                 jint cpu_affinity_policy,
                                                 jint gpu_perf_hint,
                                                 jint gpu_priority_hint,
                                                 jobject input_tensors_shapes,
                                                 jobject output_tensors_shapes,
                                                 jboolean debug) {
    LOGV("%s(), start", __func__);
    gDebug = debug;
    LOGI("debug log enable = %d", gDebug);

    _init_native_clazz_methods(env, thiz);

    /**
     * prepare the path variable
     */
    const char *model_name_ptr = env->GetStringUTFChars(model_name, nullptr);
    if (gDebug) {
        LOGD("mace_file_model name = %s", model_name_ptr);
    }

    const char *model_graph_file_path_ptr = env->GetStringUTFChars(model_graph_file_path, nullptr);
    if (gDebug) {
        LOGD("mace_file_model graph file = %s", model_graph_file_path_ptr);
    }

    const char *model_data_file_path_ptr = env->GetStringUTFChars(model_data_file_path, nullptr);
    if (gDebug) {
        LOGD("mace_file_model data file = %s", model_data_file_path_ptr);
    }

    const char *storage_path_ptr = env->GetStringUTFChars(storage_directory, nullptr);
    if (gDebug) {
        LOGD("storage path = %s", storage_path_ptr);
    }

    const char *target_runtime_ptr = env->GetStringUTFChars(target_runtime, nullptr);
    if (gDebug) {
        LOGD("target runtime = %s", target_runtime_ptr);
    }

    /**
     * create MaceContext
     */
    MaceContext *maceContext = new MaceContext;
    maceContext->model_name = model_name_ptr;
    maceContext->device_type = MaceCommon::getInstance()->parseDeviceType(target_runtime_ptr);
    maceContext->model_infos.model_graph_path = string(model_graph_file_path_ptr);
    maceContext->model_infos.model_data_path = string(model_data_file_path_ptr);

    if (gDebug) {
        LOGD("create MaceContext");
    }
    /**
     * for input tensor
     */
    jobject inputTensorsShapesKeyObj = env->CallObjectMethod(input_tensors_shapes,
                                                             gMapClass.entrySet);
    jobject inputTensorsShapesKeyIteratorObj = env->CallObjectMethod(inputTensorsShapesKeyObj,
                                                                     gSetClass.iterator);
    while (env->CallBooleanMethod(inputTensorsShapesKeyIteratorObj, gIteratorClass.hasNext)) {
        jobject entryObj = env->CallObjectMethod(inputTensorsShapesKeyIteratorObj,
                                                 gIteratorClass.next);
        jstring inputTensorName = (jstring) env->CallObjectMethod(entryObj, gMap_EntryClass.getKey);
        jintArray inputTensorShape = (jintArray) env->CallObjectMethod(entryObj,
                                                                       gMap_EntryClass.getValue);

        const char *inputTensorName_ptr = env->GetStringUTFChars(inputTensorName, nullptr);
        LOGV("input tensor name : %s", inputTensorName_ptr);
        vector<int64_t> inputShape;
        int length = env->GetArrayLength(inputTensorShape);
        int *array = env->GetIntArrayElements(inputTensorShape, 0);
        for (int i = 0; i < length; i++) {
            inputShape.push_back(array[i]);
            LOGV("input shape : %d", array[i]);
        }
        maceContext->model_infos.input_tensors.insert(
                pair<string, vector<int64_t>>(inputTensorName_ptr, inputShape));
        env->ReleaseIntArrayElements(inputTensorShape, array, 0);
        env->ReleaseStringUTFChars(inputTensorName, inputTensorName_ptr);
    }
    if (gDebug) {
        LOGD("prepare input tensor");
    }

    /**
     * for output tensor
     */
    jobject outputTensorsShapesKeyObj = env->CallObjectMethod(output_tensors_shapes,
                                                              gMapClass.entrySet);
    jobject outputTensorsShapesKeyIteratorObj = env->CallObjectMethod(outputTensorsShapesKeyObj,
                                                                      gSetClass.iterator);
    while (env->CallBooleanMethod(outputTensorsShapesKeyIteratorObj, gIteratorClass.hasNext)) {
        jobject entryObj = env->CallObjectMethod(outputTensorsShapesKeyIteratorObj,
                                                 gIteratorClass.next);
        jintArray outputTensorShape = (jintArray) env->CallObjectMethod(entryObj,
                                                                        gMap_EntryClass.getValue);
        jstring outputTensorName = (jstring) env->CallObjectMethod(entryObj,
                                                                   gMap_EntryClass.getKey);

        const char *outputTensorName_ptr = env->GetStringUTFChars(outputTensorName, nullptr);
        LOGV("output tensor name : %s", outputTensorName_ptr);
        vector<int64_t> outputShape;
        int length = env->GetArrayLength(outputTensorShape);
        int *array = env->GetIntArrayElements(outputTensorShape, 0);
        for (int i = 0; i < length; i++) {
            outputShape.push_back(array[i]);
            LOGV("output shape : %d", array[i]);
        }
        maceContext->model_infos.output_tensors.insert(
                pair<string, vector<int64_t>>(outputTensorName_ptr, outputShape));
        env->ReleaseIntArrayElements(outputTensorShape, array, 0);
        env->ReleaseStringUTFChars(outputTensorName, outputTensorName_ptr);
    }
    if (gDebug) {
        LOGD("prepare output tensor");
    }

    /**
     * set GPU runtime context
     */
    MaceEngineConfig config(maceContext->device_type);
    if (maceContext->device_type == DeviceType::GPU) {
        if (strlen(storage_path_ptr) > 0) {
            maceContext->gpu_context = GPUContextBuilder()
                    .SetStoragePath(storage_path_ptr)
//                mace tag v1.0.2 无此接口，而用tag v1.1.1则加载文件模型报错
//                    .SetOpenCLCacheReusePolicy(
//                            static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
                    .Finalize();
        } else {
            maceContext->gpu_context = GPUContextBuilder()
//                mace tag v1.0.2 无此接口，而用tag v1.1.1则加载文件模型报错
//                    .SetOpenCLCacheReusePolicy(
//                            static_cast<OpenCLCacheReusePolicy>(opencl_cache_reuse_policy))
                    .Finalize();
        }

        config.SetGPUContext(maceContext->gpu_context);
        config.SetGPUHints(
                static_cast< GPUPerfHint>( gpu_perf_hint),
                static_cast< GPUPriorityHint>( gpu_priority_hint));
        LOGV("init gpu runtime");
    }

    /**
     * set mace engine context
     */
    MaceStatus status = _init_mace_engine(maceContext, config, omp_num_threads,
                                          cpu_affinity_policy);
    if (gDebug) {
        LOGD("set mace engine context");
    }

    /**
     * release the resource
     */
    env->ReleaseStringUTFChars(model_name, model_name_ptr);
    env->ReleaseStringUTFChars(model_graph_file_path, model_graph_file_path_ptr);
    env->ReleaseStringUTFChars(model_data_file_path, model_data_file_path_ptr);
    env->ReleaseStringUTFChars(storage_directory, storage_path_ptr);
    env->ReleaseStringUTFChars(storage_directory, target_runtime_ptr);

    /**
     * dump the infomation
     */
    if (gDebug) {
        for (auto &kv: maceContext->model_infos.input_tensors) {
            string value;
            for (auto &v: kv.second) {
                value += to_string(v) + ",";
            }
            value.erase(value.length() - 1, value.length());
            LOGV("input_tensors [ %s ] = [ %s ]", kv.first.c_str(), value.c_str());
        }

        for (auto &kv: maceContext->model_infos.output_tensors) {
            string value;
            for (auto &v: kv.second) {
                value += to_string(v) + ",";
            }
            value.erase(value.length() - 1, value.length());
            LOGV("output_tensors [ %s ] = [ %s ]", kv.first.c_str(), value.c_str());
        }
    }

    LOGV("%s() exit, maceContext = %ld : 0x%lx", __func__, (jlong) (maceContext),
         (jlong) (maceContext));
    return (jlong) (maceContext);
}

jboolean jni_native_mace_file_execute(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context,
                                      jobject input_tensors,
                                      jobject output_tensors) {
    LOGV("%s() with native_mace_context = %ld : 0x%lx", __func__,
         (jlong) (native_mace_context),
         (jlong) (native_mace_context));
    MaceContext *maceContext = (MaceContext *) native_mace_context;

    _init_native_clazz_methods(env, thiz);

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

        string native_input_tensor_name(inputTensorName_ptr);
        auto tensorShape = maceContext->model_infos.input_tensors.at(native_input_tensor_name);
        env->ReleaseStringUTFChars(inputTensorName, inputTensorName_ptr);

        /**
         * tensor shape
         */
        const int tensor_size = accumulate(tensorShape.begin(), tensorShape.end(), 1,
                                           multiplies<int64_t>());

        /**
         * check input name and size
         */
        jint input_size = env->CallIntMethod(inputTensor, gFloatTensorClass.getSize);
        if (input_size != tensor_size) {
            LOGE("input_size(%d) not equal as extern tensor_size(%d)", input_size, tensor_size);
            return false;
        }
        LOGV("execute input tensor name : %s , tensor_size = %d, input_size = %d",
             native_input_tensor_name.c_str(), tensor_size, input_size);

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
        inputs[native_input_tensor_name] = MaceTensor(tensorShape, input_tensor_data);
    }

    /**
     * for output tensor
     */
    for (auto &kv: maceContext->model_infos.output_tensors) {
        auto output_tensor_name = kv.first;
        auto output_tensor_shape = kv.second;

        /**
         * tensor size
         */
        int64_t output_size =
                accumulate(output_tensor_shape.begin(), output_tensor_shape.end(), 1,
                           multiplies<int64_t>());
        auto buffer_out = shared_ptr<float>(new float[output_size],
                                            default_delete<float[]>());
        /**
         * check input name and size
         */
        outputs[output_tensor_name] = MaceTensor(output_tensor_shape, buffer_out);
    }

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

jboolean jni_native_mace_file_release(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context) {
    if (gDebug) {
        LOGD("%s() with native_mace_context = %ld : 0x%lx", __func__, (jlong) (native_mace_context),
             (jlong) (native_mace_context));
    }
    MaceContext *maceContext = (MaceContext *) native_mace_context;
    delete maceContext;
    return true;
}