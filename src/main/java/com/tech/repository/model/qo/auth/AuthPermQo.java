package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthPermQo {
    private Long id;
    private Long parentId = 0L;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String code;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String name;
    @NotNull(message = ErrorMsg.PARAM_ERROR)
    private Integer type;
    private String remark;
}
