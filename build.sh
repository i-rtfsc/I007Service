#./gradlew clean && ./gradlew assembleMlRelease && ./gradlew assembleStandardRelease
#./gradlew clean && ./gradlew standardRelease && ./gradlew mlRelease
#./gradlew clean && ./gradlew standardRelease && adb install out/release/I007Service-standard.apk
./gradlew clean && ./gradlew mlRelease && adb install out/release/I007Service-ml.apk