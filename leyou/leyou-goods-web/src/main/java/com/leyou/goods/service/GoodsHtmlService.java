package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){
        //创建上下文
        Context context=new Context();
        PrintWriter printWriter  = null;
        //将数据加入到上下文
        context.setVariables(this.goodsService.loadData(spuId));
        try {
            //把静态文件生成到服务器本地
            //创建输出流，关联到一个临时文件
            File file =new File("D:\\soft\\nginx-1.10.3\\html\\item"+spuId+".html") ;
           printWriter = new PrintWriter(file);
            this.engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter != null){
                printWriter.close();
            }
        }

    }


    /**
     * 删除静态页面
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\\\soft\\\\nginx-1.10.3\\\\html\\\\item\"+spuId+\".html");
        file.deleteOnExit();
    }
}
