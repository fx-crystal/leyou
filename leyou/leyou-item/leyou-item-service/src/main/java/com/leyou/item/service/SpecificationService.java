package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> queryParams(Long gid) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        return this.specParamMapper.select(specParam);
    }


    public void saveSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insertSelective(specGroup);
    }

    public void updateSpecGroup(SpecGroup specGroup) {
        int i =  specGroupMapper.updateByPrimaryKeySelective(specGroup);
        System.out.println("------------------------"+i);
    }

    public void deleteSpecGroup(Long id) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(id);
        specParamMapper.delete(specParam);
        specGroupMapper.deleteByPrimaryKey(id);
    }

    public void saveSpecParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    public void updateSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    public void deleteSpecParam(Long paramId) {
        specParamMapper.deleteByPrimaryKey(paramId);
    }

    /**
     * 查询规格模板中某一组的规格参数
     *
     * @param gid
     * @return
     */
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return this.specParamMapper.select(record);
    }

    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        //查询组
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        //查询组下的规格参数
        groups.forEach(group->{
            List<SpecParam> params = this.queryParams(group.getId());
            group.setSpecParams(params);
        });
        return groups;
    }
}
