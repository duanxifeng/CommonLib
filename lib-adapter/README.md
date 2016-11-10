
# RecyclerView Adapter

1. 为RecyclerView提供更简单的适配器实现方式，不断更新完善中。

2. [Demo视频演示](http://7xtjec.com1.z0.glb.clouddn.com/ittleQ.mp4)

3. [GitHub地址](https://github.com/chendongMarch/CommonLib/lib-adapter)

4. [博客](http://blog.csdn.net/chendong_/article/details/50897581)

[adapterId区分](adapterId-区分)

![](http://7xtjec.com1.z0.glb.clouddn.com/toc.png)

<a href="#test">文字</a>

* [使用](#使用)
* [BaseViewHolder的使用](#BaseViewHolder的使用)
* [通用适配器](#通用适配器)
	* [单类型数据适配](#单类型数据适配)
	* [多类型数据适配](#多类型数据适配)
	* [九宫格模式适配](#九宫格模式适配)
* [监听事件](#监听事件)
	* [三种事件](#三种事件)
	* [实现需要的事件](#实现需要的事件)
* [数据更新](#数据更新)
	* [数据更新](#数据更新)
	* [分页加载更新](#分页加载更新)
* [Module](#Module)
	* [添加Header和Footer](#添加Header和Footer)
	* [预加载更多](#预加载更多)
* [其他](#其他)
	* [adapterId区分](#adapterId区分)
	* [Sample](#Sample)


<span id="test" name="test"></span>
## test


## adapterId 区分
## Usage
- 类库还在开发中，暂时没有发布到Jcenter,所以需要在`yourProject.gradle`文件中添加如下代码进行依赖

```
allprojects {
    repositories {
         maven { url 'https://dl.bintray.com/chendongmarch/maven' }
    }
}
```

- 在`yourApp.gradle`文件中添加依赖

```
compile 'com.march.lib-adapter:lib-adapter:1.0.0'
```


## 接口介绍
类库中涉及的几个数据相关的接口
### ITypeAdapterModel
进行多类型数据适配时，Model需要实现`ITypeAdapterModel`告知Adapter数据的type
### ISectionRule
进行九宫格模式适配时，需要添加`ISectionRule`,这是一种规则，adapter会根据你提供的规则自动生成Header
### AbsSectionHeader
进行九宫格模式适配时，作为header的数据类型需要实现AbsSectionHeader


## BaseViewHolder的使用

- 通用ViewHolder，内部使用`SparseArray`实现View的缓存。

```java
//BaseViewHolder

//获取控件
public <T extends View> T getView(int resId)
public <T extends View> T getView(String resId)
//设置可见
public RvViewHolder setVisibility(int resId, int v)
//文字
public RvViewHolder setText(int resId, String txt)
//图片
public RvViewHolder setImg(int resId, int imgResId)
//监听
public RvViewHolder setClickLis(int resId, View.OnClickListener listener)
```

##

## 通用适配器
### 单类型数据适配
```java
//一个简单的实现,实体类不需要再去实现RvQuickInterface接口
SimpleRvAdapter simpleAdapter =
new SimpleRvAdapter<Demo>(self, demos, R.layout.rvquick_item_a) {
            @Override
            public void onBindView(RvViewHolder holder, Demo data, int pos) {
                holder.setText(R.id.item_a_tv, data.title);
            }
        };
```
### 多类型数据适配
```java
//调用addType()方法配置每种类型的layout资源
//实体类需要实现RvQuickInterface接口
TypeRvAdapter<Demo> typeAdapter =
new TypeRvAdapter<Demo>(context, data) {
            @Override
            public void onBindView(RvViewHolder holder, Demo data, int pos, int type) {
                //根据类型绑定数据
                switch (type) {
                    case Demo.CODE_DETAIL:
                        holder.setText(R.id.item_quickadapter_type_title, data.getmDemoTitle()).setText(R.id.item_quickadapter_desc, data.getmDescStr());
                        break;
                    case Demo.JUST_TEST:
                        holder.setText(R.id.item_quickadapter_title, data.getmDemoTitle());
                        break;
                }
            }

        };
typeAdapter.addType(Demo.CODE_DETAIL, R.layout.item_quickadapter_type)
                .addType(Demo.JUST_TEST, R.layout.item_quickadapter);
```
### 九宫格模式适配
- 每个Header下面由多个item。类似微信九宫格照片展示

- [针对多种情况的API稍微复杂一些 Details](https://github.com/chendongMarch/QuickRv/blob/master/ItemHeaderAdapter.md)

```java
// ItemHeader表示header的数据类型，Content表示内部数据的数据类型
ItemHeaderAdapter<ItemHeader, Content> adapter = new ItemHeaderAdapter<ItemHeader, Content>(
        this,
        contents,
        R.layout.item_header_header,
        R.layout.item_header_content) {
    @Override
    protected void onBindItemHeader(RvViewHolder holder, ItemHeader data, int pos, int type) {
        holder.setText(R.id.info1, data.getTitle());
    }
    @Override
    protected void onBindContent(RvViewHolder holder, Content data, int pos, int type) {
        ViewGroup.LayoutParams layoutParams = holder.getParentView().getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().widthPixels / 3.0f);
    }
};
adapter.addItemHeaderRule(new ItemHeaderRule<ItemHeader, Content>() {
    @Override
    public ItemHeader buildItemHeader(int currentPos, Content preData, Content currentData, Content nextData) {
        return new ItemHeader("pre is " + getIndex(preData) + " current is " + getIndex(currentData) + " next is " + getIndex(nextData));
    }
    @Override
    public boolean isNeedItemHeader(int currentPos, Content preData, Content currentData, Content nextData) {
        Log.e("chendong", getString(preData) + "  " + getString(currentData) + "  " + getString(nextData));
        return currentData.index % 5 == 0;
    }
});
mRv.setAdapter(adapter);
```


## 监听事件
### 三种事件
### 实现需要的事件
- 单击事件 和 长按事件,带有范型

```java
public void setOnItemClickListener(OnClickListener<D> mClickLis)

public void setOnItemLongClickListener(OnLongClickListener<D> mLongClickLis)

eg:
quickAdapter.setOnItemClickListener(new OnClickListener<Demo>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder, Demo data) {
                Toast.makeText(self, "click " + pos + "  " + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });
```

## 数据更新

- append方法为在原来的数据后面拼接数据，传入的参数不是全部的只是需要拼接的
- update和change方法传入为全部数据

```java
    //  全部更新数据,内部调用notifyDataSetChanged
    //  data为展示的全部数据
    public void updateData(List<D> data)

    //  自动计算更新插入的数据,如果是插入数据，调用该方法
    //  内部计算调用`notifyItemRangeInserted()`,避免全部刷新解决图片闪烁
    //  data为展示的全部数据，通过计算差值更新
    public void updateRangeInsert(List<D> data)

    //  添加数据并更新
    //  内部计算调用`notifyItemRangeInserted()`,避免全部刷新解决图片闪烁
    //  data应该是需要拼接的数据
    //  该方法结合分页加载使用
    public void appendDataUpdateRangeInsert(List<D> data)


    // 只想替换或拼接数据但不想更新显示，使用如下方法,然后自行调用`notifyItemChanged()`等更新方法
    public void changeDataNotUpdate(List<D> data)
    public void appendDataNotUpdate(List<D> data)
```

## Module

- 为了更好的扩展adapter，和实现功能的分离,每个模块负责自己的工作更加清晰

- 使用HFModule实现添加Header和Footer

- 使用LoadMoreModule实现预加载更多功能


## 添加Header和Footer

- Header和Footer的添加使用模块的方式,相关操作依赖于HFModule


- 资源Id设置(推荐使用这种方式,header 和 footer的数据配置可以在抽象方法中操作)

```java
HFModule hfModule =
    new HFModule(context, R.layout.header,R.layout.footer, recyclerView);
quickAdapter.addHeaderFooterModule(hfModule);
```

- 使用加载好的View设置

```java
View headerView = getLayoutInflater().inflate(R.layout.header, recyclerView,false)
View footerView = getLayoutInflater().inflate(R.layout.footer, recyclerView,false)
HFModule hfModule = new HFModule(headerView,footerView);
quickAdapter.addHFModule(hfModule);
```
- 抽象方法实现Header,Footer显示

```java
quickAdapter = new TypeRvAdapter<Demo>(self, demos) {
            @Override
            public void onBindHeader(RvViewHolder header) {
               //给Header绑定数据和事件,不需要可以不实现
            }

            @Override
            public void onBindFooter(RvViewHolder footer) {
               //给footer绑定数据和事件,不需要可以不实现
            }
        };
```
- 相关API

```java
quickAdapter.getHFModule().isHasHeader();
quickAdapter.getHFModule().isHasFooter();
// 隐藏和显示header和footer
quickAdapter.getHFModule().setFooterEnable(true);
quickAdapter.getHFModule().setHeaderEnable(true);
```

## 预加载

- 预加载模块,添加LoadMoreModule实现加载更多，当接近数据底部时会出发加载更多

- preLoadNum,表示提前多少个Item触发预加载，未到达底部时,距离底部preLoadNum个Item开始加载

- 每当到达底部时会触发加载，为防止多次加载，一次加载未完成时会禁止第二次加载，当加载结束之后调用finishLoad()，保证第二次加载可以进行。

```java
//方法
public void addLoadMoreModule(LoadMoreModule loadMore)

//当数据加载完毕时调用,才能使下次加载有效,防止重复加载
mLoadMore.finishLoad();


eg:
LoadMoreModule loadMoreModule =
    new LoadMoreModule(2, new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final LoadMoreModule mLoadMore) {
                Log.e("chendong", "4秒后加载新的数据");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            demos.add(new Demo(i, "new " + i));
                        }
                        mLoadMore.finishLoad();
                        //隐藏footer
                        quickAdapter.getHFModule().setFooterEnable(false);
                    }
                }, 4000);
            }
        });
quickAdapter.addLoadMoreModule(loadMoreModule);
```


## 使用adapterId

- 由于可以使用匿名内部类的形式快速实现,就无法通过类的instantOf方法区分,此时可以使用adapterId区分

```java
public int getAdapterId();

public void setAdapterId(int adapterId);

public boolean isUseThisAdapter(RecyclerView rv) {
        return ((RvAdapter) rv.getAdapter()).getAdapterId() == adapterId;
}
```


## 举个例子
```java

//内部类实现
quickAdapter = new TypeRvAdapter<Demo>(self, demos) {
            @Override
            public void onBindView(RvViewHolder holder, Demo data, int pos, int type) {
               // 给控件绑定数据,必须实现
            }

            @Override
            public void onBindHeader(RvViewHolder header) {
               //给Header绑定数据和事件,不需要可以不实现
            }

            @Override
            public void onBindFooter(RvViewHolder footer) {
               //给footer绑定数据和事件,不需要可以不实现
            }
        };





//继承实现
public class MyAdapter extends TypeRvAdapter<Demo> {

    public MyAdapter(Context context, List<Demo> data) {
        super(context, data);
    }

    @Override
    public void onBindView(RvViewHolder holder, Demo data, int pos, int type) {
       // 给控件绑定数据,必须实现
    }

    @Override
    public void onBindHeader(RvViewHolder header) {
       //给Header绑定数据和事件,不需要可以不实现
    }

    @Override
    public void onBindFooter(RvViewHolder footer) {
       //给footer绑定数据和事件,不需要可以不实现
    }
}
```

# License
```
Copyright 2016 chendong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
