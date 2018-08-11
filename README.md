# I007Service

## 背景
&#160; &#160; &#160; &#160;App开发过程中难免会遇到监听系统屏幕亮灭、网络状态、电池变化等需求；甚至还遇到要求识别当前运行的是否是游戏、视频、音乐等等。对于监听系统状态变化还好，如果是识别当前运行的是否是游戏就比较难了。基于这样的背景，I007Service诞生了！

## 引入
```java
implementation 'com.journeyOS:i007Service:1.1.0'
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
final long factors = I007Manager.SCENE_FACTOR_APP | I007Manager.SCENE_FACTOR_LCD;

I007Manager.registerListener(factors, new II007Listener.Stub() {
    @Override
    public void onSceneChangedJson(long factorId, String msg) throws RemoteException {
        Log.d(TAG, "on scene changed factorId = [" + factorId + "], json msg = [" + msg + "]");
        FACTORY factory = I007Manager.getFactory(factorId);
        switch (factory) {
            case APP:
                AppInfo appInfo = JsonHelper.fromJson(msg, AppInfo.class);
                boolean isGameState = I007Manager.isGame(appInfo.state);
                Log.d(TAG, "on scene changed, is game by state = [" + isGameState + "], running packageName = [" + appInfo.packageName + "]");
                break;
            case LCD:
                LcdInfo lcdInfo = JsonHelper.fromJson(msg, LcdInfo.class);
                boolean isGame = I007Manager.isGame(lcdInfo.packageName);
                Log.d(TAG, "on scene changed, is game by packageName = [" + isGame + "], running packageName = [" + lcdInfo.packageName + "]");
                if ((lcdInfo.state & I007Manager.SCENE_FACTOR_LCD_STATE_ON) != 0) {
                    Log.d(TAG, "on scene changed, screen on");
                } else if ((lcdInfo.state & I007Manager.SCENE_FACTOR_LCD_STATE_OFF) != 0) {
                    Log.d(TAG, "on scene changed, screen off");
                }
                break;
        }
    }
});
```
- factors：场景因子，在注册时把需要关心的场景传过去。如上诉代码（factors = I007Manager.SCENE_FACTOR_APP | I007Manager.SCENE_FACTOR_LCD）则表示监听app变化以及屏幕亮灭的变化
- factorId：回传回来的场景因子
- msg：回传回来的是json，解析如例子中所示