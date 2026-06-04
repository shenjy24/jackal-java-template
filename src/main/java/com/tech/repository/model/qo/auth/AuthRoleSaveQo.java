package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRoleSaveQo {
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String code;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String name;
    private String remark;
}
