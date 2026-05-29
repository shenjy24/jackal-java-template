package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_permission")
public class AuthPermissionEntity extends LogicEntity {
    @TableId(value = "perm_id", type = IdType.ASSIGN_ID)
    private Long permId;
    private String code;
    private String name;
    private Integer type;
    private String remark;
}
