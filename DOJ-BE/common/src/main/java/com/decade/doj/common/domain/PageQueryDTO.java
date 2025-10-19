package com.decade.doj.common.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;

@Data
@Schema(description = "分页查询条件")
@Accessors(chain = true)
public class PageQueryDTO {

    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer DEFAULT_PAGE_NUM = 1;
    @Schema(description = "页码")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = DEFAULT_PAGE_NUM;
    @Schema(description = "每页数量")
    @Min(value = 1, message = "每页查询数量不能小于1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    @Schema(description = "是否升序")
    private Boolean isAsc = true;
    @Schema(description = "排序字段")
    private String sortBy;

    public int from() {
        return (pageNo - 1) * pageSize;
    }

    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);
        // 是否手动指定排序方式
        if (orderItems != null && orderItems.length > 0) {
            for (OrderItem orderItem : orderItems) {
                page.addOrder(orderItem);
            }
            return page;
        }
        // 前端是否有排序字段
        if (StrUtil.isNotEmpty(sortBy)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(sortBy);
            page.addOrder(orderItem);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        if (StringUtils.isBlank(sortBy)) {
            sortBy = defaultSortBy;
            this.isAsc = isAsc;
        }
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(this.isAsc);
        orderItem.setColumn(sortBy);
        page.addOrder(orderItem);
        return page;
    }

    public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
        return toMpPage("create_time", false);
    }


}
