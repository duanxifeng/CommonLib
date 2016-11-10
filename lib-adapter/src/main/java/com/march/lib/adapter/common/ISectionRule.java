package com.march.lib.adapter.common;

/**
 * Project  : QuickRv
 * Package  : com.march.lib.adapter.inter
 * CreateAt : 16/9/2
 * Describe : 创建item header的rule
 *
 * @author chendong
 */
public interface ISectionRule<IH, ID> {

    IH buildItemHeader(int currentPos, ID preData, ID currentData, ID nextData);

    boolean isNeedItemHeader(int currentPos, ID preData, ID currentData, ID nextData);
}
