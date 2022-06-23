# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

file(GLOB all_scrs
        "${CMAKE_SOURCE_DIR}/*.h"
        "${CMAKE_SOURCE_DIR}/*.hpp"
        "${CMAKE_SOURCE_DIR}/*.cpp"
        "${CMAKE_SOURCE_DIR}/*.c"
        )

include_directories(${CMAKE_SOURCE_DIR}/)
include_directories(${CMAKE_SOURCE_DIR}/include)
file(GLOB LIBMACE "${CMAKE_SOURCE_DIR}/lib/arm64-v8a/*")
set(mace_lib ${LIBMACE})
add_library(mace_lib SHARED IMPORTED)
set_target_properties(mace_lib PROPERTIES IMPORTED_LOCATION ${mace_lib})

#file(GLOB LIBMODEL "${CMAKE_SOURCE_DIR}/model/arm64-v8a/*.a")
#set(mobilenet_lib ${LIBMODEL})
#add_library(mobilenet_lib STATIC IMPORTED)
#set_target_properties(mobilenet_lib PROPERTIES IMPORTED_LOCATION ${mobilenet_lib})

set(mobilenet_v1_lib "${CMAKE_SOURCE_DIR}/model/arm64-v8a/mobilenet_v1.a")
add_library(mobilenet_v1_lib STATIC IMPORTED)
set_target_properties(mobilenet_v1_lib PROPERTIES IMPORTED_LOCATION ${mobilenet_v1_lib})

set(mobilenet_v1_quant_lib "${CMAKE_SOURCE_DIR}/model/arm64-v8a/mobilenet_v1_quant.a")
add_library(mobilenet_v1_quant_lib STATIC IMPORTED)
set_target_properties(mobilenet_v1_quant_lib PROPERTIES IMPORTED_LOCATION ${mobilenet_v1_quant_lib})

set(mobilenet_v2_lib "${CMAKE_SOURCE_DIR}/model/arm64-v8a/mobilenet_v2.a")
add_library(mobilenet_v2_lib STATIC IMPORTED)
set_target_properties(mobilenet_v2_lib PROPERTIES IMPORTED_LOCATION ${mobilenet_v2_lib})

set(mobilenet_v2_quant_lib "${CMAKE_SOURCE_DIR}/model/arm64-v8a/mobilenet_v2_quant.a")
add_library(mobilenet_v2_quant_lib STATIC IMPORTED)
set_target_properties(mobilenet_v2_quant_lib PROPERTIES IMPORTED_LOCATION ${mobilenet_v2_quant_lib})

add_library( # Sets the name of the library.
        mace_neural_network

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${all_scrs})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        android_native

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        mace_neural_network
        mobilenet_v1_lib
        mobilenet_v1_quant_lib
        mobilenet_v2_lib
        mobilenet_v2_quant_lib
        mace_lib
        # Links the target library to the log library
        # included in the NDK.
        ${android_native})