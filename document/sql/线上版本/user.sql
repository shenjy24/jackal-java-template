CREATE TABLE `user`
(
    `user_id`       bigint            NOT NULL COMMENT '用户ID',
    `nickname`      varchar(64)       DEFAULT NULL COMMENT '用户昵称',
    `avatar`        varchar(256)      DEFAULT NULL COMMENT '用户头像',
    `phone`         char(11)          DEFAULT NULL COMMENT '手机号',
    `gender`        tinyint           DEFAULT NULL COMMENT '性别 1.男 2.女',
    `birthday`      varchar(16)       DEFAULT NULL COMMENT '生日，格式为yyyy-MM-dd',
    `address`       varchar(256)      DEFAULT NULL COMMENT '住址',
    `deleted`       tinyint           NOT NULL DEFAULT '0' COMMENT '是否删除 0否 1是',
    `create_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户信息表';

CREATE TABLE `user_token`
(
    `token_id`    bigint      NOT NULL COMMENT '逻辑主键',
    `user_id`     bigint      NOT NULL COMMENT '用户ID',
    `token`       varchar(64) NOT NULL COMMENT 'token值',
    `expire_time` datetime    NOT NULL COMMENT '过期时间',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`token_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户登录token表';

CREATE TABLE `user_account`
(
    `account_id`     bigint          NOT NULL COMMENT '逻辑主键',
    `user_id`        bigint          NOT NULL COMMENT '用户ID',
    `account`        varchar(64)     NOT NULL COMMENT '账号',
    `password`       varchar(64)     NOT NULL COMMENT '密码',
    `create_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `uk_account` (`account`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户账号表';