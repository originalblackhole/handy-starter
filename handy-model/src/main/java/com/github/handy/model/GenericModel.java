package com.github.handy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 16:47
 */
@Data
public class GenericModel<PK> implements Serializable {

    private static final long serialVersionUID = -1815670042194289042L;

    protected PK id;

    /**
     * 创建人
     */
    protected String creator;

    /**
     * 创建日期
     */
    protected Date gmtCreated;

    /**
     * 修改人
     */
    protected String modifier;

    /**
     * 修改日期
     */
    protected Date gmtModified;

    /**
     * 是否逻辑删除 char(1) comment '删除标识：N-未删除；Y-已删除'
     */
    protected String isDeleted = "N";

    protected String remark;

    protected String extraInfo;

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
        buffer.append("id='").append(getId()).append("'");
        buffer.append("]");

        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (!(o instanceof GenericModel)) {
            return false;
        }

        GenericModel other = (GenericModel) o;
        if (getId() != null && other.getId() != null) {
            if (getId() instanceof Comparable) {
                return ((Comparable) getId()).compareTo(other.getId()) == 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return  Objects.hash(getId());
    }
}
