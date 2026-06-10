package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_role")
public class AuthRoleEntity extends LogicEntity {
    private String name;
    private String remark;
}
