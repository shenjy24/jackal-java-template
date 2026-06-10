package com.tech.repository.model.qo.auth;

import lombok.Data;

import java.util.List;

@Data
public class AuthUserQo {
    private Long id;
    private String nickname;
    private String avatar;
    private String account;
    private List<Long> roleIds;
}
