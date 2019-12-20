package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> ,SelectByIdListMapper<Brand,Long> {

    @Insert("INSERT into tb_category_brand (category_id,brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long id);

    @Select("select * from tb_brand a INNER JOIN tb_category_brand b on a.id = b.brand_id where b.category_id = #{cid}")
    List<Brand> selectBrandsByCid(Long cid);

}
