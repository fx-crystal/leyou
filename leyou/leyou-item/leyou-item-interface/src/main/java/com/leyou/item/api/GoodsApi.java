package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 商品的分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(@RequestParam(value = "key", required = false) String key,
                                            @RequestParam(value = "saleable", required = false) Boolean saleable,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "rows", defaultValue = "5") Integer rows);


    /**
     * 根据spuId查询sku集合
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);

    /**
     * 根据id查询spu
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Spu queryById(@PathVariable("id") Long id);

    @GetMapping("sku/{skuId}")
    public Sku querySkuBySkuId(@PathVariable("skuId")Long skuId);
}
