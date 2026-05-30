package com.tech.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tech.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * 自动填充配置
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = TimeUtil.currentTimestamp();
        this.strictInsertFill(metaObject, "createTime", () -> now, Timestamp.class);
        this.strictInsertFill(metaObject, "updateTime", () -> now, Timestamp.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp now = TimeUtil.currentTimestamp();
        this.strictUpdateFill(metaObject, "updateTime", () -> now, Timestamp.class);
    }
}
