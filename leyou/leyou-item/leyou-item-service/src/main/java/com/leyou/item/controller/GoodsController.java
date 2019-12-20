package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    /**
     * 商品的分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    //http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(@RequestParam(value = "key", required = false) String key,
                                                              @RequestParam(value = "saleable", required = false) Boolean saleable,
                                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows)
    {
        PageResult<SpuBo> pageResult=goodsService.queryGoodsByPage(key,saleable,page,rows);
        System.out.println(pageResult.getItems());
        if(pageResult==null || pageResult.getItems()==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        this.goodsService.save(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail =  this.goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }
    /**
     * 根据spuId查询sku集合
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus =  this.goodsService.querySkusBySpuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 修改商品
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/{spuId}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("spuId")Long spuId){
        try {
            System.out.println("***********"+spuId);
            goodsService.deleteGoods(spuId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Spu> queryById(@PathVariable("id") Long id){
       Spu spu =  this.goodsService.querySpuById(id);
       if(spu == null){
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(spu);
    }


    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId")Long skuId){
        Sku sku =  this.goodsService.querySkuBySkuId(skuId);
        if(sku == null){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(sku);
    }
}
