package com.tech.repository.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tech.repository.entity.LogicEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("user")
public class UserEntity extends LogicEntity {
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 手机
     */
    private String phone;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 住址
     */
    private String address;
}
