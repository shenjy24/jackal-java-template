package com.tech.model.qo;

import com.tech.common.constant.Constants;
import lombok.Data;

/**
 * 分页查询参数
 *
 * @author shenjy
 * @since 2023/12/18 20:47
 */
@Data
public class PageQo {
    // 页码，从1开始
    private Integer pageNum = Constants.PAGE_NUM;
    // 返回条数，默认10条
    private Integer pageSize = Constants.PAGE_SIZE;
}
