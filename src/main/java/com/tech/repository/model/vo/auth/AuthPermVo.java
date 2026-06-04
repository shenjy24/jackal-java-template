package com.tech.repository.model.vo.auth;

import lombok.Data;

@Data
public class AuthPermVo {
    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private Integer type;
    private String icon;
    private String path;
    private Integer sort;
    private String remark;
}
