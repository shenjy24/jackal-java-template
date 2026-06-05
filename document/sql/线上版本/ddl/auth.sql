CREATE TABLE IF NOT EXISTS `auth_user`
(
    `id`            bigint            NOT NULL COMMENT '用户ID',
    `nickname`      varchar(50)       NOT NULL COMMENT '用户昵称',
    `avatar`        varchar(255)      DEFAULT NULL COMMENT '用户头像',
    `account`       varchar(50)       NOT NULL COMMENT '登陆账号',
    `password`      varchar(255)      NOT NULL COMMENT '登陆密码',
    `deleted`       bigint            NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT ='用户信息表';

CREATE TABLE IF NOT EXISTS `auth_user_token`
(
    `id`          bigint      NOT NULL COMMENT '逻辑主键',
    `user_id`     bigint      NOT NULL COMMENT '用户ID',
    `token`       varchar(50) NOT NULL COMMENT 'token值',
    `expire_time` datetime    NOT NULL COMMENT '过期时间',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT ='用户登录token表';

CREATE TABLE IF NOT EXISTS `auth_role` (
  `id`          bigint          NOT NULL COMMENT '角色ID',
  `name`        varchar(100)    NOT NULL COMMENT '角色名称',
  `remark`      varchar(255)    DEFAULT NULL COMMENT '备注',
  `deleted`     bigint          NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `auth_perm` (
  `id`          bigint          NOT NULL COMMENT '权限ID',
  `parent_id`   bigint          NOT NULL DEFAULT 0 COMMENT '父级权限ID',
  `code`        varchar(50)     DEFAULT NULL COMMENT '权限编码',
  `name`        varchar(50)     NOT NULL COMMENT '权限名称',
  `type`        tinyint         NOT NULL COMMENT '权限类型 1.菜单 2.按钮',
  `icon`        varchar(50)     DEFAULT NULL COMMENT '图标',
  `path`        varchar(50)     DEFAULT NULL COMMENT '链接地址',
  `sort`        int             NOT NULL DEFAULT 0 COMMENT '排序',
  `remark`      varchar(255)    DEFAULT NULL COMMENT '备注',
  `deleted`     bigint          NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT='权限表';

CREATE TABLE IF NOT EXISTS `auth_user_role` (
  `id`          bigint      NOT NULL COMMENT '主键',
  `user_id`     bigint      NOT NULL COMMENT '用户ID',
  `role_id`     bigint      NOT NULL COMMENT '角色ID',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS `auth_role_perm` (
  `id`          bigint      NOT NULL COMMENT '主键',
  `role_id`     bigint      NOT NULL COMMENT '角色ID',
  `perm_id`     bigint      NOT NULL COMMENT '权限ID',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT='角色权限关联表';

