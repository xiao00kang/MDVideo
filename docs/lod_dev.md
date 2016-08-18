# 开发日记
2016.6.12
- 在Application中添加Google multidex插件加载，方法数没这么快到65535，我只是添加玩一下

2016.6.14
- 调整toolbar为半透明风格。
- 增加播放控制，还在修改中

2016.6.15
- 修改播放页面的menu菜单，增加比例调节选项
- 阅读PLDroidPlayer文档，熟悉相关定制接口，下一步准备定制MediaController

2016.6.16
- 删除播放页面toolbar colorPrimary Alpha值得设定，API 21以后A TaskDescription's primary color should be opaque解决6.0机器上的崩溃问题（arthar）
- 修改沉浸式状态栏的适配。

2016.6.17
- 完善README文档

2016.6.20
- 更改包名为com.studyjams.mdvideo
- 删除原有PLDroidPlayer的jar、so包，播放器改用Google ExoPlayer.

2016.6.22
- 匹配播放页面的filter，支持点击SD卡中的视频拉起播放器。

2016.7.6
- 最小版本兼容提升至API 20,以便于更好的使用空间，省去适配兼容的麻烦。
- 更改主界面的框架，便于切换

2016.7.8
- 调整toolbar的滑动折叠效果
- API 兼容至19 (-_-メ)
- 添加一张图片

2016.7.12
- 合并[nthreex](https://github.com/nthreex)提交的本地视频列表部分代码
- 修改本地列表使用LoaderManager来加载媒体库数据
- 参考[RecyclerViewCursorAdapter](https://github.com/androidessence/RecyclerViewCursorAdapter)扩展RecyclerView的Adapter
- 参考[MaterialDesignExample](https://github.com/chenyangcun/MaterialDesignExample)为本地视频单个item添加onClick监听
- toolbar上增加一个搜索按钮（功能待完善）

2016.7.13
- 更改LoadManager的初始化，Fragment应该在onActivityCreated回调中初始化(-_-メ)

2016.7.15
- 删除播放页面的测试按钮与信息显示
- 修改本地视频页面单个item的布局

2016.7.18
- 集成Google Firebase

2016.7.27
- 应用内创建数据表，不直接读取Media的数据，同时增加已播放时长列，用于记录播放历史。
- 增加下拉刷新功能
- 更改应用色彩主题
- 暂时删除搜索、设置等尚未完善的功能在页面上的显示
- 本地视频页面增加数据刷新接口，在主activity中响应操作重新加载数据。
- 添加英文语言适配

2016.7.28
- 集成firebase的crash、messaging服务
- 添加分享时的github下载地址

2016.8.3
- 更新播放历史的广播改为LocalBroadcastManager，提高安全性和效率
- 更新应用分享的地址为google play地址

2016.8.4
- 调整ExoPlayer包结构，分离渲染器Renderer和MediaController
- 参考系统的MediaController，使用PopupWindow自定义一个播放控制器
- 添加播放页面控制弹窗进入与退出时的动画
- 添加播放器部分部分代码的注释

2016.8.5
- 添加播放页面的icon,替换原来系统的icon
- 添加播放页面字幕、菜单等入口的显示
- 播放页面添加时间显示

2016.8.8
- Controller中的Handler更改为弱引用
- 删除部分多余的事件监听，更改事件处理逻辑

2016.8.10
- 替换icon为SVG图片
- 添加EventBus事件总线，用来处理播放页面的部分交互逻辑。

2016.8.12
- 我突然觉得，为什么要添加一个文件选择器，然后有手动选择视频、字幕这么愚蠢的操作。因为从android系统本身的设计来讲，由于Linux的文件系统，Google并没有在原声系统上集成一个文件管理器。再者对于不熟悉Android文件系统的小白用户来说，会看不懂目录。所以播放器设计的重心在软件本身能找出系统中已存在的可用文件，然后生成列表，存储在数据库中，便于索引，这样设计，数据的整理与读取就能够解耦了，读取的时候只需要操作数据库而不是直接去读文件系统本身了。

2016.8.17
- 通过广度优先算法来遍历SD卡上的文件（暂时只实现算法部分，逻辑还未调完整）
- 通过MediaMetadataRetriever来获取视频信息

2016.8.18
- 遍历时过滤掉隐藏的缓存文件和长度为0的文件，这些文件可能是无法播放的
- 处理MediaMetadataRetriever setDataSource failed: status = 0xFFFFFFEA的bug,原因是微博缓存视频文件中有长度为0的文件。
- 关于为什么采用单线程广度优先遍历[快速目录和文件遍历](http://www.oschina.net/question/565065_75805?fromerr=9TSYJVTZ)(其实还是很慢啊)
- 更改数据的操作逻辑，全部放到IntentService中来完成。