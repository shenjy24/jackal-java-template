package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_user_role")
public class AuthUserRoleEntity extends LogicEntity {
    private Long userId;
    private Long roleId;
}
