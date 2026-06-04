package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("auth_role_perm")
public class AuthRolePermEntity extends BaseEntity {
    private Long roleId;
    private Long permId;
}
