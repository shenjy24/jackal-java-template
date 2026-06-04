package com.tech.repository.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;


/**
 * <p>
 * 用户账号表
 * </p>
 *
 * @author shenjy
 * @since 2025-08-01
 */
@Data
@TableName("user_account")
public class UserAccountEntity extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 账号
     */
    @TableField("account")
    private String account;

    /**
     * 密码
     */
    @TableField("password")
    private String password;
}
