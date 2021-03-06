package com.github.handy.util;

import lombok.Data;

/**
 * <p>成双成对的对象</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 17:45
 */
@Data
public class Pair<F, S> {

    /**
     * 第一个对象值
     */
    private F first;

    /**
     * 第二个对象值
     */
    private S second;

    public Pair() {
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
