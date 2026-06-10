package com.tech.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;
import com.tech.repository.entity.BaseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.nio.file.Paths;
import java.sql.Types;

/**
 * MyBatis Plus 自动实体代码生成器
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
public class AutoGenerator {

    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "123456";

    /** 业务模块子包，如 user、auth */
    private static final String MODULE = "user";

    /** 需要生成的表名 */
    private static final String[] TABLES = {"user_account"};

    public static void main(String[] args) {
        String javaOutputDir = Paths.get(System.getProperty("user.dir"), "src/main/java").toString();

        FastAutoGenerator.create(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD)
                .globalConfig(builder -> builder
                        .author("shenjy")
                        .outputDir(javaOutputDir)
                        .dateType(DateType.SQL_PACK)
                        .disableOpenDir())
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT || typeCode == Types.TINYINT) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        }))
                .packageConfig(builder -> builder
                        .parent("com.tech.repository")
                        .entity("entity." + MODULE)
                        .mapper("mapper." + MODULE))
                .strategyConfig(builder -> builder
                        .addInclude(TABLES)
                        .entityBuilder()
                        .enableLombok(new ClassAnnotationAttributes("@Data", "lombok.Data"))
                        .enableTableFieldAnnotation()
                        .superClass(BaseEntity.class)
                        .disableSerialVersionUID()
                        .addIgnoreColumns("deleted", "create_time", "update_time")
                        .formatFileName("%sEntity")
                        .controllerBuilder()
                        .disable()
                        .serviceBuilder()
                        .disableService()
                        .disableServiceImpl()
                        .mapperBuilder()
                        .mapperAnnotation(Mapper.class)
                        .disableMapperXml()
                        .build())
                .templateEngine(new FreemarkerTemplateEngine())
                .templateConfig(builder -> builder
                        .disable(TemplateType.CONTROLLER,
                                TemplateType.SERVICE,
                                TemplateType.SERVICE_IMPL,
                                TemplateType.XML)
                        .build())
                .execute();
    }
}
