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
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://8.138.14.210:3306/test?serverTimezone=GMT%2B8", "root", "Sjia@12346789")
                .globalConfig(builder -> {
                    builder.author("shenjy") // 设置作者
                            .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                            .dateType(DateType.SQL_PACK)
                            .disableOpenDir();
                })
                .dataSourceConfig(builder ->
                    builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                        int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                        if (typeCode == Types.SMALLINT || typeCode == Types.TINYINT) {
                            // 自定义类型转换
                            return DbColumnType.INTEGER;
                        }
                        return typeRegistry.getColumnType(metaInfo);
                    })
                )
                .packageConfig(builder -> {
                    builder.parent("com.tech.repository")
                            .entity("entity")
                            .mapper("mapper");
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user_account")
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
                            .build();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .templateConfig(builder ->
                        builder.disable(TemplateType.CONTROLLER,
                                        TemplateType.SERVICE,
                                        TemplateType.SERVICE_IMPL,
                                        TemplateType.XML)
                                .build()
                )
                .execute(); // 执行生成
    }
}