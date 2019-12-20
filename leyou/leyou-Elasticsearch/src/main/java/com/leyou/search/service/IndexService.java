package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.Repository.GoodsRepository;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 封装数据结构,存入es索引库
 */
@Service
public class IndexService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient  specClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    //JSON工具
    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //1.查询商品分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //根据id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //2.查询sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //3.查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //4.处理sku,仅封装id，价格、标题、图片、并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String,Object>> skuLists = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            //取第一张图片
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            skuLists.add(skuMap);
        });
        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = this.specClient.querySpecParam(null, spu.getCid3(), null, true);
        //根据spuId查询出spuDetail
        SpuDetail spuDetail1 = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值，进行反序列化
        Map<String,Object> genericSpecMap = mapper.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<String,Object>>(){});
        //把特殊的规格参数值，进行反序列化
        Map<String,List<Object>> specialSpecMap = mapper.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<String,List<Object>>>(){});
        Map<String,Object> specs = new HashMap<>();
        params.forEach(param->{
            //判断规格参数的类型，是否是通用的规格参数
            if(param.getGeneric()){
                //如果是通用参数类型，从genericSpecMap获取规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型，是返回区间
                if(param.getNumeric()){
                    value=chooseSegment(value,param);
                }
                specs.put(param.getName(),value);
            }else {
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }
        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //获取all字段
        goods.setAll(spu.getTitle()+" "+StringUtils.join(names," ") +" "+brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku，并转化为json字符串
        goods.setSkus(mapper.writeValueAsString(skuLists));
        //获取所有查询的规格参数{name:value}
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 把tb_spec_param表中的segments字段 解析为前端显示的可搜索范围字段
     */
    private String chooseSegment(String value, SpecParam specParam) {
        String[] split = specParam.getSegments().split(",");
        //使用NumberUtils工具类把字符串解析为double值
        Double val = NumberUtils.toDouble(value);
        String result = "其他";
        //获取每一个区间值，判断数值value是否在对应区间中
        for (String s : split) {
            String[] spl = s.split("-");
            //获取每个数值区间的起始区间
            Double begin = NumberUtils.toDouble(spl[0]);
            //因为有可能不存在结束区间，如1000以上，故end先取一个最大值
            Double end = Double.MAX_VALUE;
            //判断该数值区间是否为一个闭区间，如果为闭区间，则表示存在结束区间，应将end的取值为对应结束区间的值
            if (spl.length == 2) {
                end = NumberUtils.toDouble(spl[1]);
            }
            //获得了起始区间和结束区间后，开始判断传过来的value对应的值属于哪个区间
            if (val < end && val > begin) {
                if (spl.length == 1) {
                    result = spl[0] + specParam.getUnit() + "以上";
                } else {
                    result = s + specParam.getUnit();
                }
                break;
            }
        }
        return result;

    }

    public void save(Long id) throws IOException {
        Spu spu = this.goodsClient.queryById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }

    public void delete(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
