package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_user_role")
public class AuthUserRoleEntity extends LogicEntity {
    @TableId(value = "user_role_id", type = IdType.ASSIGN_ID)
    private Long userRoleId;
    private Long userId;
    private Long roleId;
}
