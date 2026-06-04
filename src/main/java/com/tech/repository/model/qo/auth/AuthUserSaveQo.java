package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthUserSaveQo {
    private String nickname;
    private String avatar;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String account;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String password;
}
