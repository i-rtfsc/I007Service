# I007Service

## 背景
&#160; &#160; &#160; &#160;App开发过程中难免会遇到监听系统屏幕亮灭、网络状态、电池变化等需求；甚至还遇到要求识别当前运行的是否是游戏、视频、音乐等等。对于监听系统状态变化还好，如果是识别当前运行的是否是游戏就比较难了。基于这样的背景，I007Service诞生了！

## 引入
```java
implementation 'com.journeyOS:i007Service:1.2.0'
```
> 依赖
```java
implementation 'com.journeyOS:liteprovider:1.0.0'
implementation 'com.journeyOS:litetask:1.0.5'
```


## 示例
### 初始化
```java
I007Core.getCore().running(mApplication);
```
> 必须在主线程中调用

### 申请权限
```java
if (!I007Manager.isServiceEnabled()) {
    I007Manager.openSettingsAccessibilityService();
}
```

### 监听
```java
final long factors = I007Manager.SCENE_FACTOR_APP
                | I007Manager.SCENE_FACTOR_LCD
                | I007Manager.SCENE_FACTOR_NET
                | I007Manager.SCENE_FACTOR_HEADSET
                | I007Manager.SCENE_FACTOR_BATTERY;

I007Manager.registerListener(factors, new II007Listener.Stub() {
    @Override
    public void onSceneChanged(long factorId, String status, String packageName) throws RemoteException {
        Log.d(TAG, "on scene changed factorId = [" + factorId + "], status = [" + status + "]");
        FACTORY factory = I007Manager.getFactory(factorId);
        switch (factory) {
            case APP:
                Log.d(TAG, "app has been changed, packageName = [" + packageName + "]" +
                        ", app type = [" + I007Manager.getApp(status) + "]" +
                        ", is game = [" + I007Manager.isGame2(status) + "]");
                break;
            case LCD:
                Log.d(TAG, "lcd has been changed, packageName = [" + packageName + "]" +
                        ", isScreenOn screen status = [" + I007Manager.isScreenOn(status) + "]");
                break;
            case NET:
                Log.d(TAG, "net has been changed, packageName = [" + packageName + "]" +
                        ", net status = [" + I007Manager.getNetWork(status) + "]");
                break;
            case HEADSET:
                Log.d(TAG, "headset has been changed, packageName = [" + packageName + "]" +
                        ", headset status = [" + I007Manager.isHeadSetPlug(status) + "]");
                break;
            case BATTERY:
                Log.d(TAG, "battery has been changed, packageName = [" + packageName + "]" +
                        ", battery status = [" + I007Manager.getBatteryStatus(status) + "]" +
                        ", battery level = [" + I007Manager.getBatteryLevel(status) + "]" +
                        ", battery temperature = [" + I007Manager.getBatteryTemperature(status) + "]" +
                        ", battery health = [" + I007Manager.getBatteryHealth(status) + "]" +
                        ", battery plugged = [" + I007Manager.getBatteryPlugged(status) + "]");
                break;
        }
    }
});
```
- factors：场景因子，在注册时把需要关心的场景传过去。如上诉代码（factors = I007Manager.SCENE_FACTOR_APP | I007Manager.SCENE_FACTOR_LCD）则表示监听app变化以及屏幕亮灭的变化
- factorId：回传回来的场景因子
- status：回传回来的是String，调用I007Manager接口判断各种场景
- packageName：当前运行的包名