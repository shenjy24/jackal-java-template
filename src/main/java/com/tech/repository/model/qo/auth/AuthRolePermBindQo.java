package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AuthRolePermBindQo {
    @NotNull(message = ErrorMsg.PARAM_ERROR)
    private Long roleId;
    private List<Long> permIds;
}
