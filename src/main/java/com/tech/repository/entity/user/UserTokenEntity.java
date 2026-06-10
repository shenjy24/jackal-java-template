package com.tech.repository.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("user_token")
public class UserTokenEntity extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * token
     */
    private String token;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    public UserTokenEntity(Long userId, String token, LocalDateTime expireTime) {
        this.userId = userId;
        this.token = token;
        this.expireTime = expireTime;
    }
}
