package com.tech.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.UserId;
import com.tech.config.response.bean.JsonPage;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.qo.auth.AuthIdQo;
import com.tech.repository.model.qo.auth.AuthPermQueryQo;
import com.tech.repository.model.qo.auth.AuthPermSaveQo;
import com.tech.repository.model.qo.auth.AuthPermUpdateQo;
import com.tech.repository.model.qo.auth.AuthRolePermBindQo;
import com.tech.repository.model.qo.auth.AuthRoleQueryQo;
import com.tech.repository.model.qo.auth.AuthRoleSaveQo;
import com.tech.repository.model.qo.auth.AuthRoleUpdateQo;
import com.tech.repository.model.qo.auth.AuthUserPasswordUpdateQo;
import com.tech.repository.model.qo.auth.AuthUserQueryQo;
import com.tech.repository.model.qo.auth.AuthUserRoleBindQo;
import com.tech.repository.model.qo.auth.AuthUserSaveQo;
import com.tech.repository.model.qo.auth.AuthUserUpdateQo;
import com.tech.repository.model.qo.user.LoginAccountQo;
import com.tech.repository.model.vo.auth.AuthPermVo;
import com.tech.repository.model.vo.auth.AuthRoleVo;
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

    /**
     * 分页查询后台用户
     *
     * @param qo 查询参数
     * @return 用户分页
     */
    @PostMapping("/queryAuthUser")
    public JsonPage<AuthUserVo> queryAuthUser(@RequestBody AuthUserQueryQo qo) {
        IPage<AuthUserEntity> page = authQueryService.queryAuthUser(qo.getAccount(), qo.getNickname(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthUserVoList(page.getRecords()));
    }

    /**
     * 新增后台用户
     *
     * @param qo 用户参数
     * @return 用户信息
     */
    @PostMapping("/saveAuthUser")
    public AuthUserVo saveAuthUser(@Valid @RequestBody AuthUserSaveQo qo) {
        AuthUserEntity user = authCommandService.saveAuthUser(qo.getNickname(), qo.getAvatar(), qo.getAccount(), qo.getPassword());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 修改后台用户
     *
     * @param qo 用户参数
     */
    @PostMapping("/updateAuthUser")
    public void updateAuthUser(@Valid @RequestBody AuthUserUpdateQo qo) {
        authCommandService.updateAuthUser(qo.getId(), qo.getNickname(), qo.getAvatar(), qo.getAccount());
    }

    /**
     * 删除后台用户
     *
     * @param qo 用户ID参数
     */
    @PostMapping("/deleteAuthUser")
    public void deleteAuthUser(@Valid @RequestBody AuthIdQo qo) {
        authCommandService.deleteAuthUser(qo.getId());
    }

    /**
     * 修改当前用户密码
     *
     * @param userId 当前用户ID
     * @param qo     密码参数
     */
    @PostMapping("/updatePassword")
    public void updatePassword(@UserId Long userId, @Valid @RequestBody AuthUserPasswordUpdateQo qo) {
        authCommandService.updateAuthUserPassword(userId, qo.getOldPassword(), qo.getNewPassword());
    }

    /**
     * 分页查询角色
     *
     * @param qo 查询参数
     * @return 角色分页
     */
    @PostMapping("/queryAuthRole")
    public JsonPage<AuthRoleVo> queryAuthRole(@RequestBody AuthRoleQueryQo qo) {
        IPage<AuthRoleEntity> page = authQueryService.queryAuthRole(qo.getCode(), qo.getName(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthRoleVoList(page.getRecords()));
    }

    /**
     * 新增角色
     *
     * @param qo 角色参数
     * @return 角色信息
     */
    @PostMapping("/saveAuthRole")
    public AuthRoleVo saveAuthRole(@Valid @RequestBody AuthRoleSaveQo qo) {
        AuthRoleEntity role = authCommandService.saveAuthRole(qo.getCode(), qo.getName(), qo.getRemark());
        return authAssembler.toAuthRoleVo(role);
    }

    /**
     * 修改角色
     *
     * @param qo 角色参数
     */
    @PostMapping("/updateAuthRole")
    public void updateAuthRole(@Valid @RequestBody AuthRoleUpdateQo qo) {
        authCommandService.updateAuthRole(qo.getId(), qo.getCode(), qo.getName(), qo.getRemark());
    }

    /**
     * 删除角色
     *
     * @param qo 角色ID参数
     */
    @PostMapping("/deleteAuthRole")
    public void deleteAuthRole(@Valid @RequestBody AuthIdQo qo) {
        authCommandService.deleteAuthRole(qo.getId());
    }

    /**
     * 分页查询权限
     *
     * @param qo 查询参数
     * @return 权限分页
     */
    @PostMapping("/queryAuthPerm")
    public JsonPage<AuthPermVo> queryAuthPerm(@RequestBody AuthPermQueryQo qo) {
        IPage<AuthPermEntity> page = authQueryService.queryAuthPerm(qo.getCode(), qo.getName(), qo.getType(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthPermVo(page.getRecords()));
    }

    /**
     * 新增权限
     *
     * @param qo 权限参数
     * @return 权限信息
     */
    @PostMapping("/saveAuthPerm")
    public AuthPermVo saveAuthPerm(@Valid @RequestBody AuthPermSaveQo qo) {
        AuthPermEntity perm = authCommandService.saveAuthPerm(qo.getCode(), qo.getName(), qo.getType(), qo.getRemark());
        return authAssembler.toAuthPermVo(perm);
    }

    /**
     * 修改权限
     *
     * @param qo 权限参数
     */
    @PostMapping("/updateAuthPerm")
    public void updateAuthPerm(@Valid @RequestBody AuthPermUpdateQo qo) {
        authCommandService.updateAuthPerm(qo.getId(), qo.getCode(), qo.getName(), qo.getType(), qo.getRemark());
    }

    /**
     * 删除权限
     *
     * @param qo 权限ID参数
     */
    @PostMapping("/deleteAuthPerm")
    public void deleteAuthPerm(@Valid @RequestBody AuthIdQo qo) {
        authCommandService.deleteAuthPerm(qo.getId());
    }

    /**
     * 用户绑定角色
     *
     * @param qo 用户角色绑定参数
     */
    @PostMapping("/bindUserRole")
    public void bindUserRole(@Valid @RequestBody AuthUserRoleBindQo qo) {
        authCommandService.bindUserRole(qo.getUserId(), qo.getRoleIds());
    }

    /**
     * 角色绑定权限
     *
     * @param qo 角色权限绑定参数
     */
    @PostMapping("/bindRolePerm")
    public void bindRolePerm(@Valid @RequestBody AuthRolePermBindQo qo) {
        authCommandService.bindRolePerm(qo.getRoleId(), qo.getPermIds());
    }
}
