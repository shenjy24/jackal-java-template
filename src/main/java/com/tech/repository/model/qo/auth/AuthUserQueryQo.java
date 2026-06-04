package com.tech.repository.model.qo.auth;

import com.tech.repository.model.qo.PageQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserQueryQo extends PageQo {
    private String account;
    private String nickname;
}
