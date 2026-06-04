package com.tech.repository.model.vo.auth;

import lombok.Data;

@Data
public class AuthRoleVo {
    private Long id;
    private String code;
    private String name;
    private String remark;
}
