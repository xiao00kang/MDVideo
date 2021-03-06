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

2016.8.24
- 更新 ExoPlayer 至 1.5.10，并更新部分更改的 API 调用

2016.8.25
- 扩展数据库，将搜索到的字幕文件也存入数据库中
- 增加视频信息存储的比特率、文件创建日期、横竖屏尺寸信息
- 视频列表增加字幕地址关联列表，用于记录用户自己加载过的外挂字幕

2016.8.29
- 外挂字幕在 Text渲染轨道中加载不了外挂字幕，查看了 ExoPlayer 所有与 Subtitle 相关的 issues 没有找到解决方案。但又不想再引入一个第三方库来渲染字幕，字幕加载只好先放一放了，先完成其他功能。
- 修改 MediaController 的 UI 添加播放按钮的Path动画，API不得不又切回到21，后面再考虑兼容（不得不说 UI 设计真的不好做，以一介新手的姿态，在天马行空的想法中说服自己哪个方案更好，真是太费时间了。一边写一边调尺寸和样式）
- 增加 MediaController 上播放文件名的显示
- 修复选择播放器打开文件时，因已播放时长字符串转换引起的bug
- 添加一个声源文件，打算 APP 改个名字...

2016.8.30
- 添加一个透明的 Activity 做启动页，解决应用启动过程中的黑白屏问题
- 删除悬浮的 FloatingActionButton 文件自己遍历后，这个按钮没有更多存在的价值了，暂时先不显示
- 定制了一个 Controller 专用的 Message 类型，便于 EventBus 调用
- 新建一个 VideoMenuDialog 用来在播放页面选择要切换视频（只是新建了一个类，还没完善功能）

2016.9.2
- 添加列表 item 点击后的水纹效果
- 完善 VideoMenuDialog 的业务逻辑
- 参照官方MVP框架着手改写整个项目结构。

2016.9.7
- 更改Data部分的项目结构，打算先从这一块起构建MVP的框架
- 规范数据表的命名，等开发告一段落后一定加入checkStyle

2016.9.9
- 规范化 Model 部分的业务逻辑与接口（勉强先跟整体凑上了，但是历史记录监听不到数据的变更了。查了1个小时发现是通过UUID生成的主键一直是0导致的。很好奇MVP Demo中为何不使用_id作为主键）
- 有一个疑问是数据的id是否定义成 String（而不是 int ） 的格式比较好，这样在使用SQL语句的时候就不用转换了，希望改完之后查看整体的逻辑能找到答案。

2016.9.13
- oh shit! 使用UUID生成全局唯一标识时，id应该定义为String的。没有注意生成字符串的格式，没办法直接转成int.导致了id一直是0.可以参考[全局唯一ID设计](http://www.androidchina.net/4744.html)
- 调试model层的接口，完善功能

2016.9.22
- 对于P和V的思考持续了两周左右，相关的文章也看了一些，思想是把 View 的展示和业务、事件分离出来，将Activity和Fragment转成一个View。仔细审视了这个项目的操作逻辑与业务接口
中间犯了一个错误。在思考提取Presenter接口的同时，总是伴随着加入一些新功能和交互的想法，而功能与交互逻辑的加入不可避免的就会思考各种方案并权衡。导致在这上面花费了很多时间。慢慢意识到这个问题后，决定着手写代码了。
- 参考官方的架构 to-do-mvp-loader 把 model 层封装好后，发现 P-V层的架构与这个demo并不一样。因为项目中使用了ViewPager来作为视图的层级框架。在ViewPager中Fragment的生命周期
和直接自己管理的时序有些不同，在onResume的时候presenter为null。在issues里搜了一下，与table相关的mvp实现居然有另一个demo......

2016.9.23
- 关于ViewPager + Fragment 的问题，官方源码还是三个月之前提交的，而且跟todo-mvp-provider一样的。自己想了一下，在 Activity 里初始化 Presenter 但不能保证传递
到Fragment(遇到start的时候空指针)。改在Fragment里初始化问题得以解决。
- 在官方的实践中，LoadManager的初始化是在 Presenter 的start 中进行的，而start在onResume中回调，搜索了一下 [issues](https://github.com/googlesamples/android-architecture/pull/198) 果然有因此问题提交PR的.
我把start回调改到了onActivityCreated中。
- 项目改写到这里，本地视频的加载流程改写完毕。对MVP有了重新的认识，在这里小小的总结一下。
- 从官方的实践中学习到的包结构和类的封装。这个称得上最佳实践，很多类遵循着单一职责这一原则，这其实是一个说起来简单但实践起来非常考验编程能力的原则。
- model层的封装，这也从中学到了很多。以后编程的习惯估计都用这个模版了。
- P-V层的接口调用以及业务逻辑的流程，虽然也清晰但感觉绕的好远。不好评价，在实践中再慢慢对比吧。

2016.9.29
- 完善播放记录页面的p-v接口分离。
- 尝试更新2.x版本ExoPlayer发现这个版本的MediaController绑定在SimpleExoPlayerView里了，要替换还需要自己实现一个然后替换掉controller,容我先熟悉一下2.x版本

2016.12.5
- 因工作原因一直没更新了，回头发现在ExoPlayer项目中提的关于让定制UI更简单的建议被采纳了，并且官方的博客出了一篇关于UI自定义的[文章](https://medium.com/google-exoplayer/customizing-exoplayers-ui-components-728cf55ee07a#.apkklkrac)
是时候回来继续开发这个项目了。

2016.12.8
- 时常也会思考这个项目的开发所存在的价值，除开自己本身在项目中获得技能上的提升外。我突然想到自己参加的字幕组的翻译工作。有一部分小伙伴提出了希望能在手机上翻译的需求
如果能加载外挂字幕，并开发一款字幕的编辑器，同时集成语音输入，解决手机打字困难的问题。一定能给大家带来便利。

2016.12.9
- ExoPlayer 更新到V2版本之后，确实比V1版本的使用精简了许多。主要是音视频轨道的解码都放到library里了，
然后增加了playerBackView来控制播放。之前有一个疑问，为什么mediaController要用自定义view来实现而不直接使用popupWindow。
在移植v2版本的时候才发现，popupwindow更新seekbar时必须使用handler来同步刷新ui。而View自带的post方法可以在非UI线程有效工作。
前提是view已经被添加至window中。
- 关于自定义UI的取舍，在v2的dev分支中，已经提供了替换mediaController布局文件的方法调用，并且在官方的博客中说了将在2.0.5版本中放出。
考虑到UI的更改会比较大，于是决定还是直接全部替换吧。为了保持以后的版本也能及时跟上版本的迭代。simpleExoPlayerView也从源码中拷贝出来，方便进行更改和自定义。

2016.12.13
- 切换到v2版本的exoplayer。
- 更改mediaController适配新的播放器
- 修改暂停/播放的动画，解决VectorDrawable cannot be cast to Animatable的bug
- 调整弹出列表，兼容v2版本
- mediaController动画暂时注释掉（基本动画真不可靠）

2016.12.14
- 修改播放记录中时间的中英文适配
- 修改mediaController的进出动画
- 修改播放菜单的显示bug以及播放标识
- 解决播放过了的视频不能从记录处继续播放的问题
- 版本更新至1.0.1
- 删除网络权限，firebase使用的是系统的通信。故应用本身不需要网络。

2016.12.15
- 修改mediaController的动画效果
- 修改混淆文件配置
- 根据ExoPlayer中的方法，添加一个字幕载入的方法

2016.12.21
- 完善外挂字幕加载功能，已实现加载一次字幕后与视频关联。
- 菜单添加设定选项。部分功能将放在设定里。
- 播放页面的菜单列表存在滑动冲突。
- 需要添加一个清除字幕的功能，再字幕加载错误的时候方便重新加载。

2016.12.22
- 屏蔽设定菜单准备合并到主分支
- 版本更新至1.0.3