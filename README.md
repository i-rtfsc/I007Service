# I007Service
2018年创建I007Service最初的功能是监控系统的一些状态，提供给别的进程。到2022年开始接入一些AI相关的功能，目前只是使用官方的一些模型进行落地。

## 目录结构

```bash
├── base
│   └── src
├── database
│   ├── assets
│   └── src
├── framework
│   └── src
├── gradle
│   └── wrapper
├── keystore
├── machinelearning
│   ├── core
│   │   └── src
│   ├── mace
│   │   ├── assets
│   │   ├── jniLibs
│   │   ├── script
│   │   └── src
│   ├── pytorch
│   │   ├── assets
│   │   └── src
│   ├── snpe
│   │   ├── assets
│   │   ├── libs
│   │   └── src
│   └── tflite
│       ├── assets
│       └── src
├── monitor
│   ├── res
│   └── src
├── out
│   └── release
├── platform
│   └── src
├── resource
├── scripts
├── sdk
│   ├── aidl
│   └── src
├── service
│   ├── assets
│   ├── res
│   └── src
└── test
    ├── assets
    ├── libs
    ├── res
    └── src
```

- sdk : I007Service的sdk
- base : 一些公共代码
- database : 数据相关
- framework : fwk
- monitor : 监控系统的一些状态
- machinelearning : AI相关
    - core : AI核心功能
    - tflite : tflite模型落地
    - pytorch : pytorch模型落地
    - snpe : snpe模型落地
    - mace : mace模型落地
- platform : 区分当前APK平台（如是否包含mace/snpe等等）
- service : I007Service 入口
- test : 测试APK，新的进程

## 编译
### 标准 app
```bash
./gradlew standardRelease
```

### ai app(包含所有AI模型方案)
```bash
./gradlew mlRelease
```

### tflite app
```bash
./gradlew tfRelease
```

### pytorch app
```bash
./gradlew torchRelease
```

### snpe app
```bash
./gradlew snpeRelease
```

### mace app
```bash
./gradlew maceRelease
```

## 设计思路
待补充

## tflite
待补充

## pytorch
待补充

## snpe
待补充

## mace
待补充