## MultiCamera

* 修复了的已知Bug
  1. 预览比例和GLSurfaceView比例一致 多余切割掉 类似CenterCrop
  2. 切换前后摄像头颠倒一帧
  3. 多个多个GLSurfaceView 会黑屏 (需手动调用API处理)
  4. 切换摄像头 不卡 主线程
  
* 未修复的Bug
  1. 暴力切换两个Fragment的相机预览 由于相机操作是耗时任务 会导致卡顿 (背压问题)
  2. 手动对焦不可用
  

### 第一阶段
* 基础功能
  (完成)1.Camera + GLSurfaceView 摄像头预览
  (完成)2.切换前后摄像头
  (完成)3.拍照
  (完成)4.手势触摸放大缩小
    
* 滤镜
  (完成)1.单个滤镜
  2.调试其他滤镜
  3.滤镜切换
  
* 基础功能优化
  1.Sample 的UI和结构
  2.Sample 的动态权限
  3.Camera 改为 Camera2
  
* 录制视频
  1.手动启停录制
  2.倒计时录制
  3.分段录制

* 贴纸
  1.增加贴纸(水印)
  2.删除贴纸
  3.回滚删除
  
* 直播间UI
  1. 进场特效
  2. 礼物连击
  3. 全屏礼物特效

* 视频编辑
  1.裁剪
  2.视频帧处理
  3.文字转视频
  4.添加字幕
  
### 第二阶段

* 音频剪辑
  
* 视频编辑
  1.视频间过场特效
  2.视频方向切换与旋转
  
* 根据机器学习模型 通过OpenGL 添加 特效道具

