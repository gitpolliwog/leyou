package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {


    BRAND_NOT_FOUND(400,"品牌没有找到!"),
    CATEGORY_NOT_FOUND(404,"商品分类没有查询到!"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组不存在"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在!"),
    GOODS_SPU_NOT_FOUND(404,"商品SPU不存在!"),
    SPU_DETAIL_NOT_FOUND(404,"SPU详情不存在!"),
    GOODS_SKU_FOUND(404,"商品SKU不存在!"),
    GOODS_DETAIL_FOUND(404,"商品详情不存在!"),
    GOODS_NOT_FOUND(404,"商品不存在!"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在!"),
    BRAND_SAVE_ERROR(500,"商品新增失败!"),
    FILE_UPLOAD_ERROR(500,"文件上传失败!"),
    INVALID_FILE_TYPE(400,"无效的文件类型!")
    ;
    private  int code;
    private String msg;
}
