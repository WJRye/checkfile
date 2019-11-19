# check-file-plugin
[check-file-plugin](https://github.com/WJRye/checkfile) 的主要作用是检查项目中引进的不符合要求的文件。比如，我们会限制引进的资源文件的大小，来避免终包的体积过大。

# 导入项目
在项目的 build.gradle 文件中添加依赖 `classpath 'com.wangjiang:check-file-plugin:1.0'` ：

```
buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }
    dependencies {
        classpath fileTree(dir: 'libs', include: ['*.jar'])
        classpath 'com.android.tools.build:gradle:3.1.2'
        classpath 'com.wangjiang:check-file-plugin:1.0'
    }
}
```
在主 Module 中添加：

```
apply plugin: 'com.wangjiang.check-file'

checkFile {
    maxSize = 30 (文件最大大小，大小以 K 单位)
    enable = true（开关，true表示打开，false表示关闭，默认为true）
}
```
执行命令：

```
./gradlew check-drawable
```
运行结果示例如下：

```
> Task :Kuaikan:check-drawable 
CheckFilePlugin:CheckDrawable: maxSize=30;enable=true
CheckFilePlugin:CheckDrawable: 主Module名字
Files larger than the maximum size(30 K) are as follows:
w: /Users/admin/project/workplace/kkmh-android/主Module名字/src/main/res/drawable-xhdpi/member_recharge_leave_popup_success.png(size=100.53 K)
w: /Users/admin/project/workplace/kkmh-android/主Module名字/src/main/res/drawable-xhdpi/bg_members_pay.png(size=39.07 K)
w: /Users/admin/project/workplace/kkmh-android/主Module名字/src/main/res/drawable-xhdpi/member_get_popup_success.png(size=92.65 K)
......
CheckFilePlugin:CheckDrawable: MediaPickerlibrary（主Module依赖的其它Module名字）
Files larger than the maximum size(30 K) are as follows:
w: /Users/admin/project/workplace/kkmh-android/MediaPickerlibrary/src/main/res/drawable-xhdpi/picture_loading.jpg(size=99.80 K)
......
```
