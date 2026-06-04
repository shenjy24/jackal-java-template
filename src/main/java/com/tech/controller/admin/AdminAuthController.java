package com.tech.controller.admin;

import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.UserId;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.qo.user.LoginAccountQo;
import com.tech.repository.model.vo.auth.AuthUserVo;
import com.tech.service.auth.AuthAssembler;
import com.tech.service.auth.AuthCommandService;
import com.tech.service.auth.AuthQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台用户控制器
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-02-11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AuthQueryService authQueryService;
    private final AuthCommandService authCommandService;
    private final AuthAssembler authAssembler;

    /**
     * 账号密码登陆
     *
     * @param qo 账号密码参数
     * @return 用户信息
     */
    @Anonymous
    @PostMapping("/loginByAccount")
    public AuthUserVo loginByAccount(@Valid @RequestBody LoginAccountQo qo) {
        AuthUserEntity user = authCommandService.loginByAccount(qo.getAccount(), qo.getPassword());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @PostMapping("/getUser")
    public AuthUserVo getUser(@UserId Long userId) {
        AuthUserEntity user = authQueryService.getAuthUser(userId);
        return authAssembler.toAuthUserVo(user);
    }
}
