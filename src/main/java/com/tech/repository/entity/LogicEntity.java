package com.tech.repository.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

/**
 * 逻辑删除实体
 *
 * @author shenjy
 * @since 2024/1/27 15:03
 */
@Data
public abstract class LogicEntity extends BaseEntity implements Serializable {
    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
