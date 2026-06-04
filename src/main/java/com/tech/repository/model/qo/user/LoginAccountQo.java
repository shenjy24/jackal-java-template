package com.tech.repository.model.qo.user;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 账号密码登录参数
 *
 * @author Jonas
 * @since 2025-08-01
 * @version 1.0
 */
@Data
public class LoginAccountQo {
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String account;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String password;
}
