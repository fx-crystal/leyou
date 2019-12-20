package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 商品的分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> queryGoodsByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //添加上下架的过滤条件
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加分页
         PageHelper.startPage(page, rows);
        //执行查询，获取spu
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //spu集合转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;
        }).collect(Collectors.toList());
        //返回pageResut<PageBo>
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }


    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @Transactional
    public void save(SpuBo spuBo) {
        //先新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //在新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        saveSkuAndStock(spuBo);
        sendMsg("insert",spuBo.getId());


    }

    private void sendMsg(String type,Long  id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku->{
            //新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }


    /**
     * 根据spuId查询sku集合
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        //查询每一个sku对应的库存信息
        skus.forEach(sku->{
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 修改商品
     * @param spuBo
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //根据spuId查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku->{
            //删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        //删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);
        saveSkuAndStock(spuBo);
        //更新spu和spuDetail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMsg("update",spuBo.getId());
    }


    /**
     * 删除商品
     * @param spuId
     */
    public void deleteGoods(Long spuId) {
        //先删除sku和库存信息
        deleteStockAndSku(spuId);
        //再删除spu和spu_detail信息
        spuMapper.deleteByPrimaryKey(spuId);
        spuDetailMapper.deleteByPrimaryKey(spuId);
        sendMsg("delete",spuId);
    }
    /**
     * 根据spuId删除sku和其库存信息
     *
     * @param spuId
     */
    private void deleteStockAndSku(Long spuId) {
        //查询sku
        Sku querySku = new Sku();
        querySku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(querySku);
        //如果查询的skus不为空，则拿到其id进行库存删除
        //JDK1.8新特性写法
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            stockMapper.deleteByExample(example);

        }
        skuMapper.delete(querySku);
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }


    public Sku querySkuBySkuId(Long skuId) {
        return this.skuMapper.selectByPrimaryKey(skuId);
    }
}
