package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /*
    根据父节点的id子节点
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);
    }

    public List<String> queryNameByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> cnames = new ArrayList<>();
        for (Category category : categories) {
            cnames.add(category.getName());
        }
        return cnames;
    }

}
