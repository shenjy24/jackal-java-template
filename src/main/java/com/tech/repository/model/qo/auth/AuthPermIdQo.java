package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthPermIdQo {
    @NotNull(message = ErrorMsg.PARAM_ERROR)
    private Long permId;
}
