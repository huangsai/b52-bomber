# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================================================================================
# 关键字                      描述
# keep                        保留类和类中的成员，防止被混淆或移除
# keepnames                   保留类和类中的成员，防止被混淆，成员没有被引用会被移除
# keepclassmembers            只保留类中的成员，防止被混淆或移除
# keepclassmembernames        只保留类中的成员，防止被混淆，成员没有引用会被移除
# keepclasseswithmembers      保留类和类中的成员，防止被混淆或移除，保留指明的成员
# keepclasseswithmembernames  保留类和类中的成员，防止被混淆，保留指明的成员，成员没有引用会被移除
# ============================================================================================
# 通配符      描述
# <field>     匹配类中的所有字段
# <method>    匹配类中所有的方法
# <init>      匹配类中所有的构造函数
# *           匹配任意长度字符，不包含包名分隔符(.)
# **          匹配任意长度字符，包含包名分隔符(.)
# ***         匹配任意参数类型
# ============================================================================================