package com.tech.model.vo.user;

import lombok.Data;

/**
 * UserVo
 *
 * @author shenjy
 * @since 2023/12/18 09:57
 */
@Data
public class UserVo {
    /**
     * 用户ID
     */
    private Long userId;
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
    private String birthday;
    /**
     * 住址
     */
    private String address;
}
