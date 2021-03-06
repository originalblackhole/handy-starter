package com.github.handy.service;

import com.github.handy.core.exception.BusinessException;
import com.github.handy.jdbc.mapper.GenericMapper;
import com.github.handy.model.CommonMessageCode;
import com.github.handy.model.PageCondition;
import com.github.handy.model.Pagination;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> 封装增删改查
 * 这里只提供原子操作，事务控制权交给子类
 * </p>
 * @author rui.zhou
 * @date 2018/12/4 18:34
 */
@Slf4j
public abstract class GenericService<T, PK> {

    protected GenericMapper<T, PK> genericMapper;

    public GenericService(GenericMapper<T, PK> genericMapper) {
        this.genericMapper = genericMapper;
    }

    /**
     * 插入数据
     * <p>
     * 如果主键是基于DB的方式，数据插入成功后，主键值会自动填充到输入对象中
     *
     * @param data 数据
     * @return 返回操作记录数
     */

    public int insert(T data) {
        int result = 0;
        try {
            genericMapper.setDefault(data, true);
            result = genericMapper.insert(data);
        } catch (Exception e) {
            log.error(CommonMessageCode.INSERT_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.INSERT_EXCEPTION, e);
        }

        return result;
    }

    /**
     * 插入数据，忽略值为null的字段
     *
     * @param data 数据
     * @return 返回操作记录数
     */
    public int insertSelective(T data) {
        int result = 0;
        try {
            genericMapper.setDefault(data, true);
            result = genericMapper.insertSelective(data);
        } catch (Exception e) {
            log.error(CommonMessageCode.INSERT_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.INSERT_EXCEPTION, e);
        }

        return result;
    }

    /**
     * 批量插入数据
     *
     * @param datas 数据
     * @return 返回操作记录数
     */
    public int insertBatch(List<T> datas) {
        int result = 0;
        try {
            if (datas != null) {
                for (T data : datas) {
                    genericMapper.setDefault(data, true);
                }
            }
            result = genericMapper.insertBatch(datas);
        } catch (Exception e) {
            log.error(CommonMessageCode.INSERT_BATCH_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.INSERT_BATCH_EXCEPTION, e);
        }
        return result;
    }

    /**
     * 更新数据
     * 主键为更新条件，其他为数据
     *
     * @param data 数据
     * @return 更新结果行数
     */
    public int update(T data) {
        int result = 0;
        if (data != null) {
            try {
                genericMapper.setDefault(data, false);
                result = genericMapper.update(data);
            } catch (Exception e) {
                log.error(CommonMessageCode.UPDATE_EXCEPTION.getMessage(), e);
                throw new BusinessException(CommonMessageCode.UPDATE_EXCEPTION, e);
            }
        }

        return result;
    }

    /**
     * 更新数据，忽略空字段
     * 主键为更新条件，其他非null字段为数据
     *
     * @param data 数据
     * @return 更新结果行数
     */
    public int updateSelective(T data) {
        int result = 0;
        if (data != null) {
            try {
                genericMapper.setDefault(data, false);
                result = genericMapper.updateSelective(data);
            } catch (Exception e) {
                log.error(CommonMessageCode.UPDATE_EXCEPTION.getMessage(), e);
                throw new BusinessException(CommonMessageCode.UPDATE_EXCEPTION, e);
            }
        }

        return result;
    }

    /**
     * 通过主键删除记录
     *
     * @param ids 主键
     * @return 删除行数
     */
    public int delete(PK... ids) {
        int result = 0;
        try {
            result = genericMapper.delete(ids);
        } catch (Exception e) {
            log.error(CommonMessageCode.DELETE_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.DELETE_EXCEPTION, e);
        }

        return result;
    }

    /**
     * 通过主键使记录无效（相当于逻辑删除）
     *
     * @param ids 主键
     * @return 更新结果行数
     */
    public int disable(PK... ids) {
        int result = 0;
        try {
            result = genericMapper.disable(ids);
        } catch (Exception e) {
            log.error(CommonMessageCode.DELETE_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.DELETE_EXCEPTION, e);
        }

        return result;
    }

    /**
     * 通过主键使记录有效（相当于恢复逻辑删除）
     *
     * @param ids 主键
     * @return 更新结果行数
     */
    public int enable(PK... ids) {
        int result = 0;
        try {
            result = genericMapper.enable(ids);
        } catch (Exception e) {
            log.error(CommonMessageCode.UPDATE_EXCEPTION.getMessage(), e);
            throw new BusinessException(CommonMessageCode.UPDATE_EXCEPTION, e);
        }

        return result;
    }

    /**
     * 通过主键获取数据
     *
     * @param id 主键
     * @return 一行数据
     */
    public T get(PK id) {
        T result = null;
        try {
            result = genericMapper.get(id);
        } catch (Exception e) {
            log.error(CommonMessageCode.SELECT_ONE_EXCEPTION.getMessage(), e);
        }

        return result;
    }


    /**
     * 通过主键获取数据
     *
     * @param ids 主键
     * @return List 如果无数据时，返回是长度为0的List对象
     */
    public List<T> getByIds(PK... ids) {
        List<T> result = null;
        try {
            result = genericMapper.getByIds(ids);
        } catch (Exception e) {
            log.error(CommonMessageCode.SELECT_ONE_EXCEPTION.getMessage(), e);
        }

        if (result == null) {
            result = new ArrayList<T>();
        }

        return result;
    }

    /**
     * 通过Model获取数据
     *
     * @param data Model数据，非null字段都做为条件查询
     * @return List 如果无数据时，返回是长度为0的List对象
     */
    public List<T> selectAll(T data) {
        List<T> result = null;
        try {
            result = genericMapper.selectAll(data);
        } catch (Exception e) {
            log.error(CommonMessageCode.SELECT_EXCEPTION.getMessage(), e);
        }

        if (result == null) {
            result = new ArrayList<T>();
        }

        return result;
    }

    /**
     * 通过pagination对象进行相关参数查询，获取分页（或Top n）数据
     * 默认生成的sql条件是 '='  如不满足要求，请在xml中重写 search 片段。
     *
     * @param pageCondition
     * @return 返回的结果存入pagination对象的data属性
     * 如果无数据时，返回是长度为0的List对象
     */

    public Pagination<T> search(PageCondition pageCondition) {
        return executePagination((condition) -> genericMapper.search(condition), pageCondition);
    }

    protected <D> Pagination<D> executePagination(PaginationCallback<D> callback, PageCondition pageCondition) {
        Pagination<D> pagination = new Pagination<>();
        try {
            Assert.notNull(pageCondition, "param pageCondition must not be null");
            PageHelper.startPage(pageCondition.getPageIndex(), pageCondition.getPageSize(), pageCondition.isNeedCount());
            List<D> pageResult = callback.execute(pageCondition);
            if (pageCondition.isNeedCount()) {
                Page page = (Page) pageResult;
                pagination.setRowTotal(( int ) page.getTotal());
                pagination.setPageTotal(page.getPages());
                pagination.setPageSize(page.getPageSize());
                pagination.setPageIndex(page.getPageNum());
            }
            List<D> result = new ArrayList<>();
            if (pageResult != null && pageResult.size() > 0) {
                result.addAll(pageResult);
            }
            pagination.setRows(result);
            return pagination;
        } catch (Exception e) {
            log.error(CommonMessageCode.SELECT_PAGINATION_EXCEPTION.getMessage(), e);
            return pagination;
        }
    }
}
