package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthPermUpdateQo {
    @NotNull(message = ErrorMsg.PARAM_ERROR)
    private Long id;
    private Long parentId;
    private String code;
    private String name;
    private Integer type;
    private String remark;
}
