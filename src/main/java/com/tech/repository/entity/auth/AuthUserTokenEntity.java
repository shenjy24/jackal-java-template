package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@TableName("auth_user_token")
public class AuthUserTokenEntity extends BaseEntity {
    private Long userId;
    private String token;
    private Timestamp expireTime;

    public AuthUserTokenEntity(Long userId, String token, Timestamp expireTime) {
        this.userId = userId;
        this.token = token;
        this.expireTime = expireTime;
    }
}
