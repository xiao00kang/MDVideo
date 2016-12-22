# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\androiddev\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-ignorewarnings						# 忽略警告，避免打包时某些警告出现
-optimizationpasses 5				# 指定代码的压缩级别
-dontusemixedcaseclassnames			# 是否使用大小写混合
-dontskipnonpubliclibraryclasses	# 是否混淆第三方jar
-dontpreverify                      # 混淆时是否做预校验
-verbose                            # 混淆时是否记录日志
-keepattributes *Annotation*        # 使用注解需要添加
-keepattributes Signature           # 使用了反射需要加入
-keepattributes EnclosingMethod     # 使用了反射需要加入
-renamesourcefileattribute Proguard # 将.class信息中的类名重新定义为"Proguard"字符串
-keepattributes SourceFile,LineNumberTable # 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法

#-libraryjars   libs/treecore.jar

-dontwarn android.support.v4.**     #缺省proguard 会检查每一个引用是否正确，但是第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，系统就会报错。
-dontwarn android.os.**
-keep class android.support.v4.** { *; } 		# 保持哪些类不被混淆
-keep class android.os.**{*;}

-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.widget
-keep public class * extends com.sqlcrypt.database
-keep public class * extends com.sqlcrypt.database.sqlite
-keep public class * extends com.treecore.**
-keep public class * extends de.greenrobot.dao.**

#不混淆所有的bean文件
-keep class com.studyjams.mdvideo.Data.bean**{*;}

#不混淆webView中js调用的方法名
# -keep class app.vsg3.com.hsgame.welfareModule.** {*;}

# Remove VERBOSE and DEBUG level log statements
-assumenosideeffects class android.util.Log {
    public static *** i(...);
    public static *** d(...);
    public static *** v(...);
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
}

-keepclasseswithmembernames class * {		# 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {			 # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {			 # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { #保持类成员
   public void *(android.view.View);
}

-keepclassmembers enum * {					# 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {	# 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }

#Gson
 -keepattributes *Annotation*
 -keep class sun.misc.Unsafe { *; }
 -keep class com.idea.fifaalarmclock.entity.***
 -keep class com.google.gson.stream.** { *; }

 ##########################
 ## SUPPORT LIBRARIES    ##
 ##########################

 # support-v4
 -dontwarn android.support.v4.**
 -keep class android.support.v4.app.** { *; }
 -keep interface android.support.v4.app.** { *; }

 # support-v7
 -dontwarn android.support.v7.**
 -keep class android.support.v7.internal.** { *; }
 -keep interface android.support.v7.internal.** { *; }
 -keep class android.support.v7.** { *; }

# R文件不混淆
 -keep public class com.studyjams.mdvideo.R$*{
     public static final int *;
 }

 # eventbus
 -keepattributes *Annotation*
 -keepclassmembers class ** {
     @org.greenrobot.eventbus.Subscribe <methods>;
 }
 -keep enum org.greenrobot.eventbus.ThreadMode { *; }

 # Only required if you use AsyncExecutor
 -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
     <init>(java.lang.Throwable);
 }