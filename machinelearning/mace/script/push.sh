#!/usr/bin/env bash

ANDROID_MACE_DIR=/Users/solo/code/github/I007Service/machinelearning/mace/script
ANDROID_MACE_FILE_MODE=mobilenet_v1

adb push $ANDROID_MACE_DIR/$ANDROID_MACE_FILE_MODE /sdcard/

adb shell ls -alh /sdcard/$ANDROID_MACE_FILE_MODE