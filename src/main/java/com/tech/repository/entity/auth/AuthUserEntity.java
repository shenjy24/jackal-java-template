package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("auth_user")
public class AuthUserEntity extends LogicEntity {
    private String nickname;
    private String avatar;
    private String account;
    private String password;
}
