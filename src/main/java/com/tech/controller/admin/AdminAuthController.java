package com.tech.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.Permission;
import com.tech.common.annotation.auth.UserId;
import com.tech.common.enums.auth.PermCode;
import com.tech.config.response.bean.JsonPage;
import com.tech.repository.entity.auth.AuthPermEntity;
import com.tech.repository.entity.auth.AuthRoleEntity;
import com.tech.repository.entity.auth.AuthUserEntity;
import com.tech.repository.model.qo.auth.*;
import com.tech.repository.model.qo.user.LoginAccountQo;
import com.tech.repository.model.vo.auth.AuthMenuVo;
import com.tech.repository.model.vo.auth.AuthPermVo;
import com.tech.repository.model.vo.auth.AuthRoleVo;
import com.tech.repository.model.vo.auth.AuthUserVo;
import com.tech.service.auth.AuthAssembler;
import com.tech.service.auth.AuthCommandService;
import com.tech.service.auth.AuthQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

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
     * 用户登出
     *
     * @param userId 用户ID
     */
    @PostMapping("/logoff")
    public void logoff(@UserId Long userId) {
        authCommandService.logoff(userId);
    }

    /**
     * 获取当前用户信息
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
     * 修改当前用户密码
     *
     * @param userId 当前用户ID
     * @param qo     密码参数
     */
    @PostMapping("/updatePassword")
    public void updatePassword(@UserId Long userId, @Valid @RequestBody AuthUserPasswordUpdateQo qo) {
        authCommandService.updateAuthUserPassword(userId, qo.getOldPassword(), qo.getNewPassword());
        authCommandService.logoff(userId);
    }

    /**
     * 修改当前用户信息
     *
     * @param qo 用户参数
     * @return 用户信息
     */
    @PostMapping("/updateUser")
    public AuthUserVo updateUser(@UserId Long userId, @Valid @RequestBody AuthUserUpdateQo qo) {
        AuthUserEntity user = authCommandService.updateAuthUser(userId, qo.getNickname(), qo.getAvatar(), qo.getAccount());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 获取当前用户菜单树
     *
     * @param userId 当前用户ID
     * @return 菜单树
     */
    @PostMapping("/listAuthMenu")
    public List<AuthMenuVo> listAuthMenu(@UserId Long userId) {
        List<AuthPermEntity> menus = authQueryService.listUserMenu(userId);
        return authAssembler.toAuthMenuTree(menus);
    }

    /**
     * 获取当前用户页面按钮
     *
     * @param userId 当前用户ID
     * @param qo     页面菜单参数
     * @return 按钮权限列表
     */
    @PostMapping("/listAuthButton")
    public List<AuthPermVo> listAuthButton(@UserId Long userId, @Valid @RequestBody AuthButtonQueryQo qo) {
        List<AuthPermEntity> buttons = authQueryService.listUserButton(userId, qo.getCode());
        return authAssembler.toAuthPermVo(buttons);
    }

    /**
     * 根据ID查询后台用户
     *
     * @param qo 查询参数
     * @return 用户分页
     */
    @PostMapping("/getAuthUser")
    public AuthUserVo getAuthUser(@Valid @RequestBody IdQo qo) {
        AuthUserEntity user = authQueryService.getAuthUser(qo.getId());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 分页查询后台用户
     *
     * @param qo 查询参数
     * @return 用户分页
     */
    @Permission(PermCode.USER_QUERY)
    @PostMapping("/queryAuthUser")
    public JsonPage<AuthUserVo> queryAuthUser(@RequestBody AuthUserQueryQo qo) {
        IPage<AuthUserEntity> page = authQueryService.queryAuthUser(qo.getAccount(), qo.getNickname(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthUserVo(page.getRecords()));
    }

    /**
     * 新增后台用户
     *
     * @param qo 用户参数
     * @return 用户信息
     */
    @Transactional
    @Permission(PermCode.USER_SAVE)
    @PostMapping("/saveAuthUser")
    public AuthUserVo saveAuthUser(@Valid @RequestBody AuthUserQo qo) {
        AuthUserEntity user = authCommandService.saveAuthUser(qo.getNickname(), qo.getAvatar(), qo.getAccount());
        authCommandService.bindUserRole(user.getId(), qo.getRoleIds());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 修改后台用户
     *
     * @param qo 用户参数
     * @return 用户信息
     */
    @Transactional
    @Permission(PermCode.USER_UPDATE)
    @PostMapping("/updateAuthUser")
    public AuthUserVo updateAuthUser(@Valid @RequestBody AuthUserQo qo) {
        AuthUserEntity user = authCommandService.updateAuthUser(qo.getId(), qo.getNickname(), qo.getAvatar(), qo.getAccount());
        authCommandService.bindUserRole(user.getId(), qo.getRoleIds());
        return authAssembler.toAuthUserVo(user);
    }

    /**
     * 删除后台用户
     *
     * @param qo 用户ID参数
     */
    @Permission(PermCode.USER_DELETE)
    @PostMapping("/deleteAuthUser")
    public void deleteAuthUser(@Valid @RequestBody IdQo qo) {
        authCommandService.deleteAuthUser(qo.getId());
    }

    /**
     * 重置用户密码
     *
     * @param qo 用户ID参数
     */
    @Permission(PermCode.USER_RESET)
    @PostMapping("/resetPassword")
    public void resetPassword(@Valid @RequestBody IdQo qo) {
        authCommandService.resetAuthUserPassword(qo.getId());
    }

    /**
     * 根据ID查询角色
     *
     * @param qo 查询参数
     * @return 角色信息
     */
    @PostMapping("/getAuthRole")
    public AuthRoleVo getAuthRole(@RequestBody IdQo qo) {
        AuthRoleEntity role = authQueryService.getAuthRole(qo.getId());
        return authAssembler.toAuthRoleVo(role);
    }

    /**
     * 分页查询角色
     *
     * @param qo 查询参数
     * @return 角色分页
     */
    @Permission(PermCode.ROLE_QUERY)
    @PostMapping("/queryAuthRole")
    public JsonPage<AuthRoleVo> queryAuthRole(@RequestBody AuthRoleQueryQo qo) {
        IPage<AuthRoleEntity> page = authQueryService.queryAuthRole(qo.getName(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthRoleVo(page.getRecords()));
    }

    /**
     * 新增角色
     *
     * @param qo 角色参数
     * @return 角色信息
     */
    @Transactional
    @Permission(PermCode.ROLE_SAVE)
    @PostMapping("/saveAuthRole")
    public AuthRoleVo saveAuthRole(@Valid @RequestBody AuthRoleQo qo) {
        AuthRoleEntity role = authCommandService.saveAuthRole(qo.getName(), qo.getRemark());
        authCommandService.bindRolePerm(role.getId(), qo.getPermIds());
        return authAssembler.toAuthRoleVo(role);
    }

    /**
     * 修改角色
     *
     * @param qo 角色参数
     * @return 角色信息
     */
    @Transactional
    @Permission(PermCode.ROLE_UPDATE)
    @PostMapping("/updateAuthRole")
    public AuthRoleVo updateAuthRole(@Valid @RequestBody AuthRoleQo qo) {
        AuthRoleEntity role = authCommandService.updateAuthRole(qo.getId(), qo.getName(), qo.getRemark());
        authCommandService.bindRolePerm(role.getId(), qo.getPermIds());
        return authAssembler.toAuthRoleVo(role);
    }

    /**
     * 删除角色
     *
     * @param qo 角色ID参数
     */
    @Permission(PermCode.ROLE_DELETE)
    @PostMapping("/deleteAuthRole")
    public void deleteAuthRole(@Valid @RequestBody IdQo qo) {
        authCommandService.deleteAuthRole(qo.getId());
    }

    /**
     * 查询权限
     *
     * @param qo 查询参数
     * @return 角色分页
     */
    @PostMapping("/getAuthPerm")
    public AuthPermVo getAuthPerm(@Valid @RequestBody IdQo qo) {
        AuthPermEntity perm = authQueryService.getAuthPerm(qo.getId());
        return authAssembler.toAuthPermVo(perm);
    }

    /**
     * 分页查询权限
     *
     * @param qo 查询参数
     * @return 权限分页
     */
    @Permission(PermCode.PERM_QUERY)
    @PostMapping("/queryAuthPerm")
    public JsonPage<AuthPermVo> queryAuthPerm(@RequestBody AuthPermQueryQo qo) {
        IPage<AuthPermEntity> page = authQueryService.queryAuthPerm(qo.getCode(), qo.getName(), qo.getType(), qo.getPageNum(), qo.getPageSize());
        return new JsonPage<>(page.getTotal(), authAssembler.toAuthPermVo(page.getRecords()));
    }

    /**
     * 获取全部权限树
     *
     * @return 权限树
     */
    @PostMapping("/listAuthPerm")
    public List<AuthMenuVo> listAuthPerm() {
        List<AuthPermEntity> perms = authQueryService.listAuthPerm();
        return authAssembler.toAuthPermTree(perms);
    }

    /**
     * 获取角色权限树
     *
     * @param qo 角色ID参数
     * @return 带选中状态的权限树
     */
    @PostMapping("/listRolePerm")
    public List<AuthMenuVo> listRolePerm(@Valid @RequestBody IdQo qo) {
        List<AuthPermEntity> perms = authQueryService.listAuthPerm();
        Set<Long> checkedIds = authQueryService.listRolePermId(qo.getId());
        return authAssembler.toAuthPermTree(perms, checkedIds);
    }

    /**
     * 新增权限
     *
     * @param qo 权限参数
     * @return 权限信息
     */
    @Permission(PermCode.PERM_SAVE)
    @PostMapping("/saveAuthPerm")
    public AuthPermVo saveAuthPerm(@Valid @RequestBody AuthPermQo qo) {
        AuthPermEntity perm = authCommandService.saveAuthPerm(qo.getParentId(), qo.getCode(), qo.getName(), qo.getType(), qo.getIcon(), qo.getPath(), qo.getSort(), qo.getRemark());
        return authAssembler.toAuthPermVo(perm);
    }

    /**
     * 修改权限
     *
     * @param qo 权限参数
     * @return 权限信息
     */
    @Permission(PermCode.PERM_UPDATE)
    @PostMapping("/updateAuthPerm")
    public AuthPermVo updateAuthPerm(@Valid @RequestBody AuthPermQo qo) {
        AuthPermEntity perm = authCommandService.updateAuthPerm(qo.getId(), qo.getParentId(), qo.getCode(), qo.getName(), qo.getType(), qo.getIcon(), qo.getPath(), qo.getSort(), qo.getRemark());
        return authAssembler.toAuthPermVo(perm);
    }

    /**
     * 删除权限
     *
     * @param qo 权限ID参数
     */
    @Permission(PermCode.PERM_DELETE)
    @PostMapping("/deleteAuthPerm")
    public void deleteAuthPerm(@Valid @RequestBody IdQo qo) {
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
