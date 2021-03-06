package com.github.handy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.validation.Valid;

/**
 * <p>分页查询条件类</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 16:52
 */
@Data
public final class PageCondition<C> {
    @Valid
    private C condition;

    private int pageIndex = 1;

    private int pageSize = 10;

    @JsonIgnore
    private boolean isNeedCount = true; //是否进行总记录统计
}
