package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;


    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, boolean desc, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example=new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause = sortBy + ( desc ? " desc" : " asc" );
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo<Brand> info = new PageInfo<>(list);
        return  new PageResult<>(info.getTotal(),list);
    }

//    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        brand.setId(null);
        int insert = brandMapper.insert(brand);
        if(insert!=1){
            throw new LyException (ExceptionEnum.BRAND_SAVE_ERROR);
        }

        for (Long cid : cids){
            insert = brandMapper.insertCategoryBrand(brand.getId(), cid);
            if(insert!=1){
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }

    }

    public Brand queryById(Long id){

        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
                throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }
}
