package com.github.handy.service;


import com.github.handy.model.PageCondition;

import java.util.List;

/**
 * <p>
 * 分页组件回调接口，这里定义钩子方法
 * 用于回调用户自己的查询方法
 * </p>
 * @author rui.zhou
 * @date 2018/12/4 18:37
 */
@FunctionalInterface
public interface PaginationCallback<T> {
    /**
     * 钩子方法，执行用户自己的查询
     */
    List<T> execute(PageCondition condition);
}
