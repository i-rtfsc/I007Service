#!/usr/bin/env bash

set -e -u -o pipefail

##  echo "|==============|====================|"
##  echo "|   parameter  |  lib will linked   |"
##  echo "|==============|====================|"
##  echo "|   dynamic    |    libmace.so      |"
##  echo "|--------------|--------------------|"
##  echo "|   static     |    libmace.a       |"
##  echo "|--------------|--------------------|"
MACE_LINK_TYPE=dynamic

MACE_DIR=/Users/solo/code/3rd/ai/mace/
ANDROID_MACE_DIR=/Users/solo/code/github/I007Service/machinelearning/mace/script

pushd $MACE_DIR

#TARGET_ABI=arm64-v8a,armeabi-v7a,arm64
TARGET_ABI=arm64-v8a
LIBRARY_DIR=$ANDROID_MACE_DIR/cpp/
INCLUDE_DIR=$LIBRARY_DIR/include
LIBMACE_DIR=$LIBRARY_DIR/lib/$TARGET_ABI/
LIBGNUSTL_SHARED_SO=libgnustl_shared.so
LIBCPP_SHARED_SO=libc++_shared.so

JNILIBS_DIR=$ANDROID_MACE_DIR/jniLibs/$TARGET_ABI
rm -rf $JNILIBS_DIR

if [ $MACE_LINK_TYPE == "dynamic" ]; then
  BAZEL_LIBMACE_TARGET=mace/libmace:libmace.so
  BAZEL_GEN_LIBMACE_PATH=bazel-bin/mace/libmace/libmace.so
elif [ $MACE_LINK_TYPE == "static" ]; then
  BAZEL_LIBMACE_TARGET=mace/libmace:libmace_static
  BAZEL_GEN_LIBMACE_PATH=bazel-genfiles/mace/libmace/libmace.a
else
  Usage
  exit 1
fi

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet.yml --target_abis=$TARGET_ABI

rm -rf $INCLUDE_DIR && mkdir -p $INCLUDE_DIR
rm -rf $LIBMACE_DIR && mkdir -p $LIBMACE_DIR
rm -rf $LIBRARY_DIR/model/

cp -rf $MACE_DIR/include/mace $INCLUDE_DIR
cp -rf $MACE_DIR/build/mobilenet/include/mace/public/*.h $INCLUDE_DIR/mace/public/
cp -rf $MACE_DIR/build/mobilenet/model $LIBRARY_DIR

bazel build --config android --config optimization $BAZEL_LIBMACE_TARGET --define cpu_enabled=true --define neon=true --define opencl=true --define quantize=true --cpu=$TARGET_ABI
cp -rf $BAZEL_GEN_LIBMACE_PATH $LIBMACE_DIR

if [ $MACE_LINK_TYPE == "dynamic" ]; then
  mkdir -p $JNILIBS_DIR
  cp -rf $BAZEL_GEN_LIBMACE_PATH $JNILIBS_DIR

  if [[ "" != `$ANDROID_NDK_HOME/ndk-depends $BAZEL_GEN_LIBMACE_PATH | grep $LIBGNUSTL_SHARED_SO` ]]; then
    cp -rf $ANDROID_NDK_HOME/sources/cxx-stl/gnu-libstdc++/4.9/libs/$TARGET_ABI/$LIBGNUSTL_SHARED_SO $JNILIBS_DIR
  fi

  if [[ "" != `$ANDROID_NDK_HOME/ndk-depends $BAZEL_GEN_LIBMACE_PATH | grep $LIBCPP_SHARED_SO` ]]; then
    cp -rf $ANDROID_NDK_HOME/sources/cxx-stl/llvm-libc++/libs/$TARGET_ABI/$LIBCPP_SHARED_SO $JNILIBS_DIR
  fi
fi

popd

pushd $MACE_DIR

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v1-code.yml
cp $MACE_DIR/build/mobilenet_v1/model/$TARGET_ABI/mobilenet_v1.a LIBRARY_DIR/model/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v1_quant-code.yml
cp $MACE_DIR/build/mobilenet_v1_quant/model/$TARGET_ABI/mobilenet_v1.a LIBRARY_DIR/model/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v2-code.yml
cp $MACE_DIR/build/mobilenet_v2/model/$TARGET_ABI/mobilenet_v2.a LIBRARY_DIR/model/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v2_quant-code.yml
cp $MACE_DIR/build/mobilenet_v2_quant/model/$TARGET_ABI/mobilenet_v2_quant.a LIBRARY_DIR/model/$TARGET_ABI

popd