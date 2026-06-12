package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_perm")
public class AuthPermEntity extends LogicEntity {
    private Long parentId;
    private String code;
    private String name;
    private Integer type;
    private String icon;
    private String path;
    private String component;
    private Integer sort;
    private String remark;
}
