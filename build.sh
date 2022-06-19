#./gradlew assembleStandardRelease && ./gradlew assembleMlRelease
#./gradlew standardRelease && ./gradlew mlRelease

#./gradlew standardRelease && adb install out/release/I007Service-standard.apk
#./gradlew mlRelease && adb install out/release/I007Service-ml.apk
#./gradlew tfRelease && adb install out/release/I007Service-tf.apk
#./gradlew torchRelease && adb install out/release/I007Service-torch.apk
./gradlew snpeRelease && adb install out/release/I007Service-snpe.apk
