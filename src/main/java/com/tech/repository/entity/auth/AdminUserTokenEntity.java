package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@TableName("admin_user_token")
public class AdminUserTokenEntity extends BaseEntity {
    @TableId(value = "token_id", type = IdType.ASSIGN_ID)
    private Long tokenId;
    private Long userId;
    private String token;
    private Timestamp expireTime;

    public AdminUserTokenEntity(Long userId, String token, Timestamp expireTime) {
        this.userId = userId;
        this.token = token;
        this.expireTime = expireTime;
    }
}
