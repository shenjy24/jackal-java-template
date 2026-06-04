package com.tech.repository.model.vo.auth;

import lombok.Data;

@Data
public class AuthUserVo {
    private Long id;
    private String nickname;
    private String avatar;
    private String account;
}
