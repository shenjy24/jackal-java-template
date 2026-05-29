package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_role_permission")
public class AuthRolePermissionEntity extends LogicEntity {
    @TableId(value = "role_perm_id", type = IdType.ASSIGN_ID)
    private Long rolePermId;
    private Long roleId;
    private Long permId;
}
