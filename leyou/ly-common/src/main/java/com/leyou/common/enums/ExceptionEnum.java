package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {


    BRAND_NOT_FOND(400,"品牌没有找到!"),
    CATEGORY_NOT_FOND(404,"商品分类没有查询到!"),
    BRAND_SAVE_ERROR(500,"商品新增失败!")
    ;
    private  int code;
    private String msg;
}
