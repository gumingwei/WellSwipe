#指定代码的压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#忽略警告
-ignorewarning
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#优化时允许访问并修改类和类的成员的 访问修饰符，可能作用域会变大。
-allowaccessmodification
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,InnerClasses
#把执行后的类重新放在某一个目录下，后跟一个目录名
-repackageclasses ''
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment

########混淆保护自己项目的部分代码以及引用的第三方jar包library########
#### Studio 不需要-libraryjars，添加会报错 java.io.IOException: The same input jar xxx.jar is specified twice.

#友盟 http://bbs.umeng.com/thread-5446-1-1.html
-keepclassmembers class * {   public <init>(org.json.JSONObject);}

# GreenDao rules# Source: http://greendao-orm.com/documentation/technical-faq#
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;}
-keep class **$Properties

#忽略警告
-dontwarn android.support.**
#-dontwarn com.baidu.**-dontwarn cn.sharesdk.**
#-dontwarn com.alibaba.fastjson.**

########混淆保护自己项目的部分代码以及引用的第三方jar包library-end#####

#### keep setters in Views so that animations can still work.
#see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
    !public void onClick(android.view.View);}

# Preserve static fields of inner classes of R classes that might be accessed# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);}

#保持 native 方法不被混淆 see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;}

# Explicitly preserve all serialization members. The Serializable interface# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();}


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
##---------------End: proguard configuration for Gson  ----------####

####记录生成的日志数据,gradle build时在本项目根目录输出########
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######