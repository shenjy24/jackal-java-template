package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_role")
public class AuthRoleEntity extends LogicEntity {
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    private Long roleId;
    private String code;
    private String name;
    private String remark;
}
