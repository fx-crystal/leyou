package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;


    /**
     *对品牌进行分页查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化example
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据name模糊查询，或者根据首字母查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //添加分页条件
        PageHelper.startPage(page,rows);
        // 添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause=sortBy+(desc?" desc":" asc");
            example.setOrderByClause(orderByClause);
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
        return  new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand
        this.brandMapper.insertSelective(brand);
        //再新增中间表
            cids.forEach(cid->{
                this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
            });
    }


    public List<Brand> queryBrandsByCids(Long cid) {
        return  this.brandMapper.selectBrandsByCid(cid);
    }

    public Brand queryBrandById(Long id) {

        return this.brandMapper.selectByPrimaryKey(id);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
       /* List<Brand> brands = new ArrayList<>();
        for (Long id : ids) {
            Brand brand = brandMapper.selectByPrimaryKey(id);
            brands.add(brand);
        }*/
        return  brandMapper.selectByIdList(ids);
    }
}
