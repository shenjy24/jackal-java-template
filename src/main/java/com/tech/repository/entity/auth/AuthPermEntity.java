package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_perm")
public class AuthPermEntity extends LogicEntity {
    private String code;
    private String name;
    private Integer type;
    private String remark;
}
