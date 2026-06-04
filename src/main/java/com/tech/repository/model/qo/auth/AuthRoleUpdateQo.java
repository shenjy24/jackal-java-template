package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRoleUpdateQo {
    @NotNull(message = ErrorMsg.PARAM_ERROR)
    private Long id;
    private String code;
    private String name;
    private String remark;
}
