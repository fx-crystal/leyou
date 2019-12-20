package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id参数组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 查询规格模板中某一组的规格参数
     *
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value = "gid", required = false) Long gid,
                                                          @RequestParam(value = "cid", required = false) Long cid,
                                                          @RequestParam(value = "searching", required = false) Boolean searching,
                                                          @RequestParam(value = "generic", required = false) Boolean generic) {
        List<SpecParam> specParams = specificationService.querySpecParams(gid, cid,searching,generic);
        if (CollectionUtils.isEmpty(specParams)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(specParams);
    }


    //http://api.leyou.com/api/item/spec/group

    /**
     * 新增规格模板的分组
     *
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup) {
        specificationService.saveSpecGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 修改规格模板中某一分组信息
     *
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup) {
        System.out.println(specGroup.getName());
        specificationService.updateSpecGroup(specGroup);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 删除规格模板中的某一分组
     *
     * @param id
     * @return
     */
    //http://api.leyou.com/api/item/spec/group/15
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Long id) {
        specificationService.deleteSpecGroup(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 新增规格模板中某一组的规格参数
     *
     * @param specParam
     * @return
     */
    //http://api.leyou.com/api/item/spec/param
    @PostMapping("param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam) {
        specificationService.saveSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 修改规格参数中某一分组的某个规格参数
     *
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam) {
        specificationService.updateSpecParam(specParam);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 删除规格参数中某一分组中的某个模板参数
     *
     * @param paramId
     * @return
     */
    @DeleteMapping("param/{paramId}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("paramId") Long paramId) {
        specificationService.deleteSpecParam(paramId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = this.specificationService.queryGroupsWithParam(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(groups);

    }
}
