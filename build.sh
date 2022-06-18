#./gradlew clean && ./gradlew assembleStandardRelease && ./gradlew assembleMlRelease
#./gradlew clean && ./gradlew standardRelease && ./gradlew mlRelease

#./gradlew clean && ./gradlew standardRelease && adb install out/release/I007Service-standard.apk
./gradlew clean && ./gradlew mlRelease && adb install out/release/I007Service-ml.apk
#./gradlew clean && ./gradlew tfRelease && adb install out/release/I007Service-tf.apk
#./gradlew clean && ./gradlew torchRelease && adb install out/release/I007Service-torch.apk