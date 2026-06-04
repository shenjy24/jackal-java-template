package com.tech.repository.model.qo.auth;

import com.tech.common.constant.ErrorMsg;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AuthRoleQo {
    private Long id;
    @NotBlank(message = ErrorMsg.PARAM_ERROR)
    private String name;
    private String remark;
    private List<Long> permIds;
}
