package com.tech.repository.model.vo.auth;

import lombok.Data;

import java.util.List;

@Data
public class AuthMenuVo {
    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private String icon;
    private String path;
    private Integer sort;
    private String remark;
    private Integer checked;
    private List<AuthMenuVo> children;
}
