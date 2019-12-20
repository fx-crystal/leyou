package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     *对品牌进行分页查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    //http://api.leyou.com/api/item/brand/page?key=&page=1&rows=5&sortBy=id&desc=false
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(@RequestParam(value = "key", required = false) String key, @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows, @RequestParam(value = "sortBy", required = false) String sortBy,
                                                              @RequestParam(value = "desc", defaultValue = "false") Boolean desc) {
        PageResult<Brand> pageResult = brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增品牌 QS:可以把一个json对象转变为查询字符串
     */
    @PostMapping  //传递的参数时一个json对象只能用一个对象接收@RequestBody Brand brand  接收多个要加this.$qs.stringify
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids")List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类的id查询品牌列表
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid")Long cid){
        List<Brand> brands =  this.brandService.queryBrandsByCids(cid);
        if (CollectionUtils.isEmpty(brands)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        Brand brand =  this.brandService.queryBrandById(id);
        if(brand==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand>brands=brandService.queryBrandByIds(ids);
        if(brands==null||brands.size()==0){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(brands);
    }

}
