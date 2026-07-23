insert into `auth_user` (`id`, `nickname`, `avatar`, `account`, `password`) values (1, 'admin', 'https://ielts-dev.oss-cn-hangzhou.aliyuncs.com/image/b5c033f289cb48799185339148fd5361.jpeg', 'admin', '$2a$10$g2zXjMERqberEn9nG98RKO/mlXTtMPiMGwmJxGwu2EDoqszGK2EGq');

-- 超级管理员角色
insert into `auth_role` (`id`, `name`, `remark`) values (1, '超级管理员', '拥有全部权限');

-- 权限管理菜单
insert into `auth_perm` (`id`, `parent_id`, `code`, `name`, `type`, `icon`, `path`, `component`, `sort`, `remark`) values
(101, 0, 'auth:manage', '权限管理', 1, 'setting', '/auth', null, 1, null),
(102, 101, 'auth:user', '后台用户管理', 2, 'user', '/auth/user', 'system/UserManageView', 1, null),
(103, 101, 'auth:role', '角色管理', 2, 'team', '/auth/role', 'system/RoleManageView', 2, null),
(104, 101, 'auth:perm', '权限管理', 2, 'lock', '/auth/perm', 'system/MenuManageView', 3, null);

-- 后台用户管理按钮
insert into `auth_perm` (`id`, `parent_id`, `code`, `name`, `type`, `icon`, `path`, `sort`, `remark`) values
(201, 102, 'auth:user:query', '查询用户', 3, null, null, 1, null),
(202, 102, 'auth:user:save', '新增用户', 3, null, null, 2, null),
(203, 102, 'auth:user:update', '更新用户', 3, null, null, 3, null),
(204, 102, 'auth:user:delete', '删除用户', 3, null, null, 4, null),
(205, 102, 'auth:user:reset', '重置密码', 3, null, null, 5, null);

-- 角色管理按钮
insert into `auth_perm` (`id`, `parent_id`, `code`, `name`, `type`, `icon`, `path`, `sort`, `remark`) values
(301, 103, 'auth:role:query', '查询角色', 3, null, null, 1, null),
(302, 103, 'auth:role:save', '新增角色', 3, null, null, 2, null),
(303, 103, 'auth:role:update', '更新角色', 3, null, null, 3, null),
(304, 103, 'auth:role:delete', '删除角色', 3, null, null, 4, null);

-- 权限管理按钮
insert into `auth_perm` (`id`, `parent_id`, `code`, `name`, `type`, `icon`, `path`, `sort`, `remark`) values
(401, 104, 'auth:perm:query', '查询权限', 3, null, null, 1, null),
(402, 104, 'auth:perm:save', '新增权限', 3, null, null, 2, null),
(403, 104, 'auth:perm:update', '更新权限', 3, null, null, 3, null),
(404, 104, 'auth:perm:delete', '删除权限', 3, null, null, 4, null);

-- 超级管理员绑定 admin 用户
insert into `auth_user_role` (`id`, `user_id`, `role_id`) values (1, 1, 1);

-- 超级管理员绑定全部权限
insert into `auth_role_perm` (`id`, `role_id`, `perm_id`) values
(1, 1, 101),
(2, 1, 102),
(3, 1, 103),
(4, 1, 104),
(5, 1, 201),
(6, 1, 202),
(7, 1, 203),
(8, 1, 204),
(9, 1, 205),
(10, 1, 301),
(11, 1, 302),
(12, 1, 303),
(13, 1, 304),
(14, 1, 401),
(15, 1, 402),
(16, 1, 403),
(17, 1, 404);
