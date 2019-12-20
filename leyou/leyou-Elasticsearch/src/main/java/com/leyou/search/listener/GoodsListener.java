package com.leyou.search.listener;

import com.leyou.search.service.IndexService;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsListener {
    @Autowired
    private SearchService searchService;
    @Autowired
    private IndexService indexService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.SEARCH.SAVE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC),
            key={"item.insert","item.update"}
    ))
    public void save(Long id) throws IOException {
        if(id == null){
            return;
        }
        this.indexService.save(id);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "MALL.SEARCH.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "MALL.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC),
            key={"item.delete"}
    ))
    public void delete(Long id) throws IOException {
        if(id == null){
            return;
        }
        this.indexService.delete(id);
    }

}
