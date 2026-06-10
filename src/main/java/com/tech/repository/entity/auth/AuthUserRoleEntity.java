package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("auth_user_role")
public class AuthUserRoleEntity extends BaseEntity {
    private Long userId;
    private Long roleId;
}
