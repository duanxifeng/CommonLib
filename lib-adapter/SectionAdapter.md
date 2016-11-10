## ItemHeaderAdapter

![](http://7xtjec.com1.z0.glb.clouddn.com/item_header_small.png)

- 针对某个Header下有多个数据，类似微信朋友圈九宫格图片展示的好友动态列表

- 基于TypeRvAdapter实现，本质上还是分类适配，只是针对Header做了简化

- 目前支持Grid和Linear.瀑布流开发中

- 有两种构造方式
    1. 一种使用有序的LinkedHashMap构建header和content的匹配顺序，数据需要在外面生成
    2. 使用ItemHeaderRule,即一种规则，自动生成插入Header数据，最大的程度上对外简化Header的操作


## 定义实体类
```java
    class ItemHeader extends RvQuickItemHeader {
        String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ItemHeader(String title) {
            this.title = title;
        }
    }

    static class Content{

        String title;

        public Content(String title) {
            this.title = title;
        }
    }
```

## 四种构造方法

1. 使用ItemHeaderRule自动匹配插入header，同时配置headerLayoutId,contentLayoutId,针对只有一种content类型的适配
2. 使用ItemHeaderRule自动匹配插入header，同时配置headerLayoutId,针对多种content类型的适配
3. 使用LinkedHashMap构建数据映射,同时配置headerLayoutId,contentLayoutId,针对只有一种content类型的适配
4. 使用LinkedHashMap构建数据映射,同时配置headerLayoutId,针对多种content类型的适配

- 如果有选择了多种类型content的构造方法，使用`addType()`添加类型，详见[TypeRvAdapter](https://github.com/chendongMarch/QuickRv#多类型)

```java
    //使用ItemHeaderRule自动匹配插入header
    //同时配置headerLayoutId,contentLayoutId,针对只有一种content类型的适配
    public ItemHeaderAdapter(Context context,List<ID> originDatas,int headerLayoutId, int contentLayoutId)

    //使用ItemHeaderRule自动匹配插入header
    //同时配置headerLayoutId,针对多种content类型的适配
    public ItemHeaderAdapter(Context context, List<ID> originDatas,int headerLayoutId)

    //使用LinkedHashMap构建数据映射
    //同时配置headerLayoutId,contentLayoutId,针对只有一种content类型的适配
    public ItemHeaderAdapter(Context context,LinkedHashMap<IH, List<ID>> originDatas,int headerLayoutId, int contentLayoutId)

    //使用LinkedHashMap构建数据映射
    //同时配置headerLayoutId,针对多种content类型的适配
    public ItemHeaderAdapter(Context context,LinkedHashMap<IH, List<ID>> originDatas,int headerLayoutId)
```

## 一、使用map的方式添加

```java
// 构建adapter
LinkedHashMap<ItemHeader, List<Content>> map = new LinkedHashMap<>();
ItemHeaderAdapter<ItemHeader, Content> adapter =
        new ItemHeaderAdapter<ItemHeader, Content>(this,map,R.layout.header,R.layout.content) {
    @Override
    protected void onBindItemHeader(RvViewHolder holder, ItemHeader data, int pos, int type) {
        // 绑定Header显示
        holder.setText(R.id.info1, data.getTitle());
    }
    @Override
    protected void onBindContent(RvViewHolder holder, Content data, int pos, int type) {
        // 绑定内容数据显示
        ViewGroup.LayoutParams layoutParams = holder.getParentView().getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().widthPixels / 3.0f);
    }
};
```


##  二、使用ItemHeaderRule自动生成Header

### Rule接口

- 我们之所以添加添加Header很多情况下是数据之间存在一些关系需要分类展示，采用配置规则（ItemHeaderRule）的方式可以自动生成Header.

```java
// rule接口，他决定了是否需要生成Header和生成什么样的Header
// currentPos当前数据的位置
// preData,currentData,nextData 前一个，当前，后一个数据
// isCheckAppendData 是不是在拼接数据，在拼接数据时也会调用这个接口，拼接时为true
public interface ItemHeaderRule<IH, ID> {
    // 构建header
    IH buildItemHeader(int currentPos,ID preData, ID currentData, ID nextData);
    // 根据前后数据判断是不是需要header
    boolean isNeedItemHeader(int currentPos,ID preData, ID currentData, ID nextData,boolean isCheckAppendData);
}

```


### 如何配置规则?

```java
private List<Content> contents = new ArrayList<>();

adapter = new ItemHeaderAdapter<ItemHeader, Content>(
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

            }
        };

        });

adapter.addItemHeaderRule(new ItemHeaderRule<ItemHeader, Content>() {
            @Override
            public ItemHeader buildItemHeader(int currentPos, Content preData, Content currentData, Content nextData) {
                // 使用数据，创建一个ItemHeader,他应该实现RvQuickItemHeader接口
                return new ItemHeader("pre is " + getIndex(preData) + " current is " + getIndex(currentData) + " next is " + getIndex(nextData));
            }

            @Override
            public boolean isNeedItemHeader(int currentPos, Content preData, Content currentData, Content nextData, boolean isCheckAppendData) {
                // 针对当前数据，前一个数据，后一个数据进行比较决定此时需不需要创建Header
                // currentPos = 0时，preData为null
                // currentPos = list.size()-1时，nextData为null

                // 当前是0且不是拼接数据或者当前的index被7正处时添加header
                return currentPos == 0 && !isCheckAppendData || currentData.index % 7 == 1;
            }
        });
```

## 监听事件

- 当使用构造方法配置header和content的layoutId时
- 默认的header数据type是`RvAdapter.TYPE_ITEM_HEADER`
- 默认的content的数据type是`RvAdapter.TYPE_ITEM_DEFAULT`

```java
adapter.setOnChildClickListener(new OnClickListener<RvQuickModel>() {
            @Override
            public void onItemClick(int pos, RvViewHolder holder, RvQuickModel data) {
                //  点击content
                if (data.getRvType() == RvAdapter.TYPE_ITEM_DEFAULT) {
                    Content content = data.get();
                    Toast.makeText(ItemHeaderRuleActivity.this, content.title, Toast.LENGTH_SHORT).show();
                }else if(data.getRvType() == RvAdapter.TYPE_ITEM_HEADER){
                    // 点击header,do some thing
                    return;
                }
            }
        });
```

## 数据

### 全量更新
- 使用该方法将会替换原来的所有数据，内置`notifyDataSetChanged();`

```java
public void updateDataAndItemHeader(List<ID> data)
public void updateDataAndItemHeader(Map<IH, List<ID>> map)
```

### 增量更新
- 结合分页加载是非常好的选择
- data的数据是新的数据,内部会直接拼接在原来的数据后面，更新使用`notifyItemRangeInserted()`避免全部更新界面闪烁
- 仅支持使用`ItemHeaderRule`生成，后面会增加map的

```java
public void appendDataAndUpdate(List<ID> data)
```


### 获取数据
```java
// 获取展示的数据集合，包含已经插入的header数据
List<RvQuickModel> datas = adapter.getDatas();
// 获取原始数据，全量或增量更新的数据都会同步，这个数据就是只包含content的数据集，并非用来展示的数据
List<Content> originDatas = adapter.getOriginDatas();
```






