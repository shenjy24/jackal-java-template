package com.tech.repository.model.qo.auth;

import lombok.Data;

@Data
public class AuthUserUpdateQo {
    private String nickname;
    private String avatar;
    private String account;
}
