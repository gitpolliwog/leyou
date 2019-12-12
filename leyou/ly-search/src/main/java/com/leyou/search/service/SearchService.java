package com.leyou.search.service;
import	java.util.HashMap;
import	java.util.Map;
import java.util.*;
import	java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandclient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;


    public Goods buildGoods(Spu spu){
        //查询分类
        List<Category> categories = categoryClient.queryByids(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())
        );
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandclient.queryBrandById(spu.getBrandId());
        if (brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //搜索字段
        String all=spu.getSubTitle()+StringUtils.join(names," ");

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_FOUND);
        }

        //对ku进行处理
        Set<Long> priceSet=new HashSet<>();
        List<Map<String,Object>> skus=new ArrayList<>();
        for (Sku sku : skuList){
            Map<String, Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            //处理价格
            priceSet.add(sku.getPrice());
        }

        //查询商品规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spu.getId());
        if(spuDetail==null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_FOUND);
        }
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        //规格参数
        Map<String,Object> specs=new HashMap<>();
        for (SpecParam param : params){
            //规格名称
            String key = param.getName();
            Object value="";
            //判断是否是通用规格
            if(param.getGeneric()){
                value=genericSpec.get(param.getId());
            }else {
                value=specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key,value);
        }

            //构建Goods对象
            Goods goods=new Goods();
            goods.setBrandId(spu.getBrandId());
            goods.setCid1(spu.getCid1());
            goods.setCid2(spu.getCid2());
            goods.setCid3(spu.getCid3());
            goods.setCreateTime(spu.getCreateTime());
            goods.setId(spu.getId());
            goods.setSubTitle(spu.getSubTitle());
            goods.setAll(all);//  搜索字段，包含标题分类，品牌，规格等。
            goods.setPrice(priceSet);//所有sku的价格集合
            goods.setSkus(JsonUtils.serialize(skuList));//所有sku的集合JSON格式
            goods.setSpecs(specs);//所有可搜索的规格参数

        return goods;
    }
}
