package com.tech.controller.web;

import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.UserId;
import com.tech.model.qo.user.LoginAccountQo;
import com.tech.model.vo.user.UserVo;
import com.tech.repository.entity.user.UserEntity;
import com.tech.service.user.UserAssembler;
import com.tech.service.user.UserCommandService;
import com.tech.service.user.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-02-11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/web/user")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserAssembler userAssembler;

    /**
     * 账号密码登陆
     *
     * @param qo 账号密码参数
     * @return 用户信息
     */
    @Anonymous
    @PostMapping("/loginByAccount")
    public UserVo loginByAccount(@Valid @RequestBody LoginAccountQo qo) {
        UserEntity user = userCommandService.loginByAccount(qo.getAccount(), qo.getPassword());
        return userAssembler.toUserVo(user);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @PostMapping("/getUser")
    public UserVo getUser(@UserId Long userId) {
        UserEntity user = userQueryService.getUser(userId);
        return userAssembler.toUserVo(user);
    }

}
