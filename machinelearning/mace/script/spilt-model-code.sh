#!/usr/bin/env bash

TARGET_ABI=arm64-v8a
MACE_DIR=/Users/solo/code/3rd/ai/mace
ANDROID_MACE_DIR=/Users/solo/code/github/I007Service/machinelearning/mace/script
LIBRARY_MODEL_DIR=$ANDROID_MACE_DIR/cpp/model

pushd $MACE_DIR

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v1-code.yml
cp $MACE_DIR/build/mobilenet_v1/model/$TARGET_ABI/mobilenet_v1.a $LIBRARY_MODEL_DIR/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v1_quantized-code.yml
cp $MACE_DIR/build/mobilenet_v1_quantized/model/$TARGET_ABI/mobilenet_v1_quantized.a $LIBRARY_MODEL_DIR/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v2-code.yml
cp $MACE_DIR/build/mobilenet_v2/model/$TARGET_ABI/mobilenet_v2.a $LIBRARY_MODEL_DIR/$TARGET_ABI

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v2_quantized-code.yml
cp $MACE_DIR/build/mobilenet_v2_quantized/model/$TARGET_ABI/mobilenet_v2_quantized.a $LIBRARY_MODEL_DIR/$TARGET_ABI

popd