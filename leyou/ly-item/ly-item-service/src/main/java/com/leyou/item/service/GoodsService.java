package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategaryService categaryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNoneBlank(key)){
                 criteria.andLike("title","%"+key+"%");
        }
        if(saleable!=null){
                criteria.andEqualTo("saleable",saleable);
        }
        example.setOrderByClause("last_update_time desc");
        List<Spu> list = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }

        LoadCategoryAndBrandName(list);

        PageInfo<Spu> pageInfo = new PageInfo<Spu>(list);

        return new PageResult<Spu>(pageInfo.getTotal(),list);

    }

    private void LoadCategoryAndBrandName(List<Spu> list) {
        for (Spu spu:list) {
            List<String> names = categaryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            spu.setBname( brandService.queryById(spu.getBrandId()).getName());
        }
    }

    public SpuDetail queryDetailById(Long id) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(id);
        if(detail==null){
            throw new LyException(ExceptionEnum.SPU_DETAIL_NOT_FOUND);
        }
        return detail;
    }

    public List<Sku> querySkuBySpuId(Long id) {

        Sku sku=new Sku();
        sku.setSpu_id(id);
        List<Sku> skuList = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_FOUND);
        }
        for (Sku s : skuList) {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            if (stock==null){
                throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }
        }
        return skuList;
    }
}
