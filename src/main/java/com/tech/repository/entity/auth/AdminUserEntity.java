package com.tech.repository.entity.auth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

@Data
@TableName("admin_user")
public class AdminUserEntity extends LogicEntity {
    @TableId
    private Long userId;
    private String nickname;
    private String avatar;
    private String account;
    private String password;
}
