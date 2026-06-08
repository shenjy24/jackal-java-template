CREATE TABLE `user`
(
    `id`            bigint            NOT NULL COMMENT '用户ID',
    `nickname`      varchar(50)       DEFAULT NULL COMMENT '用户昵称',
    `avatar`        varchar(255)      DEFAULT NULL COMMENT '用户头像',
    `phone`         char(11)          DEFAULT NULL COMMENT '手机号',
    `gender`        tinyint           DEFAULT NULL COMMENT '性别 1.男 2.女',
    `birthday`      varchar(15)       DEFAULT NULL COMMENT '生日，格式为yyyy-MM-dd',
    `address`       varchar(255)      DEFAULT NULL COMMENT '住址',
    `deleted`       bigint            NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT ='用户信息表';

CREATE TABLE `user_token`
(
    `id`          bigint      NOT NULL COMMENT '逻辑主键',
    `user_id`     bigint      NOT NULL COMMENT '用户ID',
    `token`       varchar(50) NOT NULL COMMENT 'token值',
    `expire_time` datetime    NOT NULL COMMENT '过期时间',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT ='用户登录token表';

CREATE TABLE `user_account`
(
    `id`             bigint          NOT NULL COMMENT '逻辑主键',
    `user_id`        bigint          NOT NULL COMMENT '用户ID',
    `account`        varchar(50)     NOT NULL COMMENT '账号',
    `password`       varchar(255)    NOT NULL COMMENT '密码',
    `create_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC COMMENT ='用户账号表';