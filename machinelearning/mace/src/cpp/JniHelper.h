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

#ifndef _JNI_HELPER_H
#define _JNI_HELPER_H

//对应的java包名
static const char *native_library_class_path_name = "com.journeyOS.mace.internal.NativeMace";

jstring jni_native_get_mace_version(JNIEnv *env, jclass thiz);

jstring jni_native_get_model_version(JNIEnv *env, jclass thiz, jstring model_name_str);

jstring jni_native_get_input_tensor_name(JNIEnv *env, jclass thiz, jstring model_name_str);

jintArray jni_native_get_input_tensor_shape(JNIEnv *env, jclass thiz, jstring model_name_str);

jstring jni_native_get_output_tensor_name(JNIEnv *env, jclass thiz, jstring model_name_str);

jintArray jni_native_get_output_tensor_shape(JNIEnv *env, jclass thiz, jstring model_name_str);

jint jni_native_create_network(JNIEnv *env, jclass thiz, jstring storage_path,
                               jstring opencl_cache_full_path, jint opencl_cache_reuse_policy);

jint jni_native_create_engine(JNIEnv *env, jclass thiz, jstring model_name_str, jstring device,
                              jint omp_num_threads, jint cpu_affinity_policy,
                              jint gpu_perf_hint, jint gpu_priority_hint);

jfloatArray jni_native_execute(JNIEnv *env, jclass thiz, jfloatArray input_data);

/**
 * 有部分同学说看了文档还是不知道signature改怎么写
 * 很简单 javap -s ./intermediates/runtime_library_classes_dir/mlDebug/com/journeyOS/mace/internal/NativeNetwork.class 即可
 */
static JNINativeMethod native_library_methods[] = {
        //name：Java中函数的名字
        //signature：描述了函数的参数和返回值（Java类型跟C类型对应表可自行网上查询）
        //fnPtr：C/C++的函数名
        {"nativeGetRuntimeVersion",    "()Ljava/lang/String;",                        (void *) jni_native_get_mace_version},
        {"nativeGetModelVersion",      "(Ljava/lang/String;)Ljava/lang/String;",      (void *) jni_native_get_model_version},
        {"nativeGetInputTensorName",   "(Ljava/lang/String;)Ljava/lang/String;",      (void *) jni_native_get_input_tensor_name},
        {"nativeGetInputTensorShape",  "(Ljava/lang/String;)[I",                      (void *) jni_native_get_input_tensor_shape},
        {"nativeGetOutputTensorName",  "(Ljava/lang/String;)Ljava/lang/String;",      (void *) jni_native_get_output_tensor_name},
        {"nativeGetOutputTensorShape", "(Ljava/lang/String;)[I",                      (void *) jni_native_get_output_tensor_shape},
        {"nativeMaceCreateNetwork",    "(Ljava/lang/String;Ljava/lang/String;I)I",    (void *) jni_native_create_network},
        {"nativeMaceCreateEngine",     "(Ljava/lang/String;Ljava/lang/String;IIII)I", (void *) jni_native_create_engine},
        {"nativeMaceExecute",          "([F)[F",                                      (void *) jni_native_execute},
};

#endif //_JNI_HELPER_H
