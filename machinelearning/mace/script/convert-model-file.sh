#!/usr/bin/env bash

MACE_DIR=/Users/solo/code/3rd/ai/mace
ANDROID_MACE_DIR=/Users/solo/code/github/I007Service/machinelearning/mace/script
LIBRARY_MODEL_DIR=$ANDROID_MACE_DIR/model_file

pushd $MACE_DIR

python $MACE_DIR/tools/converter.py convert --config=$ANDROID_MACE_DIR/mobilenet_v1-file.yml
mkdir -p /Users/solo/code/github/I007Service/machinelearning/mace/script/model_file
cp $MACE_DIR/build/mobilenet-v1/model/* $LIBRARY_MODEL_DIR/

popd