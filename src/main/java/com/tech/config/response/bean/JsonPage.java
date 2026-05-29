package com.tech.config.response.bean;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页返回结构
 *
 * @author shenjy
 * @since 2020/8/14
 */
@Data
public class JsonPage<T> {
    // 总条数
    private Long total;
    // 当前页内容
    private List<T> content;

    public JsonPage() {
        this.total = 0L;
        this.content = Collections.emptyList();
    }

    public JsonPage(Long total, List<T> content) {
        this.total = total;
        this.content = content;
    }
}
