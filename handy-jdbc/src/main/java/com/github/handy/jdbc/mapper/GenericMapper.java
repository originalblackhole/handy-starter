package com.github.handy.jdbc.mapper;

import com.github.handy.core.constant.Constant;
import com.github.handy.model.GenericModel;
import com.github.handy.model.PageCondition;
import com.github.handy.util.Context;

import java.util.Date;
import java.util.List;

/**
 * <p>基础mapper接口，封装简单的增删改查操作</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:00
 */
public interface GenericMapper<T, PK> {

    /**
     * 插入数据
     * @param data 数据
     * @return 返回操作记录数
     */
    int insert(T data);

    /**
     * 插入数据，忽略空字段
     * @param data 数据
     * @return 返回操作记录数
     */
    int insertSelective(T data);

    /**
     * 批量插入数据
     * @param datas 数据
     * @return 返回操作记录数
     */
    int insertBatch(List<T> datas);

    /**
     * 更新数据
     * 主键为更新条件，其他为数据
     * @param data 数据
     * @return 更新结果行数
     */
    int update(T data);

    /**
     * 更新数据，忽略空字段
     * 主键为更新条件，其他非空字段为数据
     * @param data 数据
     * @return 更新结果行数
     */
    int updateSelective(T data);

    /**
     * 通过主键删除记录 （危险！物理删除）
     * @param ids  主键
     * @return    删除行数
     */
    int delete(PK... ids);


    /**
     * 通过主键使记录无效（逻辑删除）
     * @param ids  主键
     * @return    更新结果行数
     */
    int disable(PK... ids);

    /**
     * 通过主键使记录有效（相当于恢复逻辑删除）
     * @param ids  主键
     * @return    更新结果行数
     */
    int enable(PK... ids);

    /**
     * 通过主键获取数据
     * @param id  主键
     * @return    一行数据
     */
    T get(PK id);

    /**
     * 通过主键获取数据
     * @param ids  主键
     * @return List 如果无数据时，返回是长度为0的List对象
     */
    List<T> getByIds(PK... ids);

    /**
     * 通过Model获取数据
     * @param data  Model数据，非空字段都做为条件查询
     * @return    数据列表
     */
    List<T> selectAll(T data);

    /**
     * 通过pagination对象进行相关参数查询，获取分页（或Top n）数据
     * @param condition
     * @return    数据列表
     */
    List<T> search(PageCondition condition);


    /**
     * 设置默认值方法
     * @param data  数据实体
     * @param isNew 是否是新增，如果新增，就设置创建时间，创建人  否则，只设置修改时间，修改人
     */
    default void setDefault(T data, boolean isNew){
        if (data instanceof GenericModel) {
            GenericModel model = ( GenericModel ) data;
            Context context = Context.getCurrentContext();

            if (isNew) {
                if (model.getGmtCreated() == null) {
                    model.setGmtCreated(new Date());
                }

                if (context != null && context.get(Constant.USER_NAME_KEY_IN_CONTEXT) != null) {
                    model.setCreator(( String ) context.get(Constant.USER_NAME_KEY_IN_CONTEXT));
                }
            }

            model.setGmtModified(new Date());


            if (context != null && context.get(Constant.USER_NAME_KEY_IN_CONTEXT) != null) {
                model.setModifier(( String ) context.get(Constant.USER_NAME_KEY_IN_CONTEXT));
            }

            if (model.getIsDeleted() == null) {
                model.setIsDeleted("N");
            }
        }
    }
}
