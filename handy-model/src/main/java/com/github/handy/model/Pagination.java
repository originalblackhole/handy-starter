package com.github.handy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 16:54
 */
@Data
public class Pagination<D> {

    ////当前返回的记录列表
    private List<D> rows = new ArrayList<>();

    //总记录数
    private int rowTotal = 0;

    //总页数
    private int pageTotal = 0;

    //当前页数
    private int pageIndex = 1;

    //每页条数
    private int pageSize = 10;

    @JsonIgnore
    private boolean needCount = true; //是否进行总记录统计

    @JsonIgnore
    private PageCondition pageCondition;

    public Pagination() {

    }

    public Pagination(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Pagination(int pageIndex, int pageSize, boolean needCount) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.needCount = needCount;
    }

    //根据pageCondition对象构造Pagination
    public Pagination(PageCondition pageCondition) {
        if (pageCondition != null) {
            this.pageSize = pageCondition.getPageSize();
            this.pageIndex = pageCondition.getPageIndex();
            this.needCount = pageCondition.isNeedCount();
            this.pageCondition = pageCondition;
        }
    }

    /**
     * 基于DO对象转换器把当前实例转换为DTO分页对象
     *
     * @param converter DO对象转换器
     * @param <R> DTO对象的类型
     */
    public <R> Pagination<R> transformWithDoConverter(DoConverter<D, R> converter) {
        Pagination<R> target = new Pagination<>();
        copyCommonProperty(target);
        target.setRows(this.getRows().stream().map(converter::convert).collect(Collectors.toList()));
        return target;
    }

    /**
     * 基于DO列表转换器把当前实例转换为DTO分页对象
     *
     * @param converter DO列表转换器
     * @param <R> DTO对象的类型
     */
    public <R> Pagination<R> transformWithListConverter(ListConverter<D, R> converter) {
        Pagination<R> target = new Pagination<>();
        copyCommonProperty(target);
        target.setRows(converter.convert(this.getRows()));
        return target;
    }

    /**
     * 拷贝通用属性
     */
    private <R> void copyCommonProperty(Pagination<R> target) {
        target.setPageIndex(this.getPageIndex());
        target.setPageSize(this.getPageSize());
        target.setNeedCount(this.isNeedCount());
        target.setRowTotal(this.getRowTotal());
        target.setPageCondition(this.getPageCondition());
        target.setPageTotal(this.getPageTotal());
    }

    /**
     * DO对象转DTO
     *
     * @param <S> DO对象类型
     * @param <T> DTO对象类型
     */
    @FunctionalInterface
    public interface DoConverter<S, T> {

        T convert(S s);
    }

    /**
     * DO列表转换为DTO列表
     *
     * @param <S> DO对象类型
     * @param <T> DTO对象类型
     */
    @FunctionalInterface
    public interface ListConverter<S, T> {

        List<T> convert(List<S> s);
    }
}
