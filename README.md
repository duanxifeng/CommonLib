# 快速开发框架


## Usage
<1> [lib-core](https://github.com/chendongMarch/CommonLib/lib-core)

核心库，集成了开发必须的核心功能

```java
compile 'com.march.lib-core:lib-core:1.0.1'
```


<2> [lib-support](https://github.com/chendongMarch/CommonLib/lib-support)

支持库，集成了一些帮助类和简化开发的类

```java
compile 'com.march.lib-support:lib-support:1.0.0'
```


<3> [lib-adapter](https://github.com/chendongMarch/CommonLib/lib-adapter)

RecyclerView的通用适配器，提供单类型、多类型和九宫格模式的适配效果，可以添加Header和Footer,上拉加载更多。

```java
compile 'com.march.lib-adapter:lib-adapter:1.0.0'
```

<4> [lib-view](https://github.com/chendongMarch/CommonLib/lib-view)

自定义控件的库，里面有一些自定义的控件

```java
compile 'com.march.lib-view:lib-view:1.0.0'
```


- 类库正在开发中并没有发不到JCenter,需要先在项目根目录下的` yourProject/build.gradle `文件中添加如下依赖

```java
allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven { url 'https://dl.bintray.com/chendongmarch/maven' }
    }
}
```
- 类库中使用25.0.0的android-support库,如果与你的版本不一致你可以在`app/build.gradle`中使用如下代码过滤掉
```java
compile ('com.march.lib-core:lib-core:1.0.1,{
    exclude group: 'com.android.support', module: 'support-annotations'
})
```











