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

jobject jni_native_mace_code_get_model_info(JNIEnv *env, jclass thiz,
                                            jstring model_name_str);

jlong jni_native_mace_code_create_network_engine(JNIEnv *env, jclass thiz,
                                                 jstring model_name_str,
                                                 jstring target_runtime,
                                                 jstring storage_path,
                                                 jint opencl_cache_reuse_policy,
                                                 jint omp_num_threads,
                                                 jint cpu_affinity_policy,
                                                 jint gpu_perf_hint,
                                                 jint gpu_priority_hint,
                                                 jboolean debug);

jfloatArray jni_native_mace_code_execute_float(JNIEnv *env, jclass thiz,
                                               jlong native_mace_context,
                                               jfloatArray input_data);

jboolean jni_native_mace_code_execute(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context,
                                      jobject input_tensors,
                                      jobject output_tensors);

jboolean jni_native_mace_code_release(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context);

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
                                                 jboolean debug);

jboolean jni_native_mace_file_execute(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context,
                                      jobject input_tensors,
                                      jobject output_tensors);

jboolean jni_native_mace_file_release(JNIEnv *env, jobject thiz,
                                      jlong native_mace_context);

/**
 * 有部分同学说看了文档还是不知道signature改怎么写
 * 很简单 javap -s ./intermediates/runtime_library_classes_dir/maceDebug/com/journeyOS/mace/internal/NativeMace.class 即可
 */
static JNINativeMethod
        native_library_methods[] = {
        //name：Java中函数的名字
        //signature：描述了函数的参数和返回值（Java类型跟C类型对应表可自行网上查询）
        //fnPtr：C/C++的函数名
        {"nativeGetMaceVersion",              "()Ljava/lang/String;",                                                                                                              (void *) jni_native_get_mace_version},
        //mace code model
        {"nativeMaceCodeGetModelInfo",        "(Ljava/lang/String;)Lcom/journeyOS/mace/internal/NativeMace;",                                                                      (void *) jni_native_mace_code_get_model_info},
        {"nativeMaceCodeCreateNetworkEngine", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIIIZ)J",                                                                   (void *) jni_native_mace_code_create_network_engine},
        {"nativeMaceCodeExecuteFloat",        "(J[F)[F",                                                                                                                           (void *) jni_native_mace_code_execute_float},
        {"nativeMaceCodeExecute",             "(JLjava/util/Map;Ljava/util/Map;)Z",                                                                                                (void *) jni_native_mace_code_execute},
        {"nativeMaceCodeRelease",             "(J)Z",                                                                                                                              (void *) jni_native_mace_code_release},
        //mace file model
        {"nativeMaceFileCreateNetworkEngine", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIIILjava/util/Map;Ljava/util/Map;Z)J", (void *) jni_native_mace_file_create_network_engine},
        {"nativeMaceFileExecute",             "(JLjava/util/Map;Ljava/util/Map;)Z",                                                                                                (void *) jni_native_mace_file_execute},
        {"nativeMaceFileRelease",             "(J)Z",                                                                                                                              (void *) jni_native_mace_file_release},
};

#endif //_JNI_HELPER_H
