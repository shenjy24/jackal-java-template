package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthButtonQueryQo {
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String code;
}
