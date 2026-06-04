package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AuthUserQo {
    private Long id;
    private String nickname;
    private String avatar;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String account;
    private String password;
    private List<Long> roleIds;
}
