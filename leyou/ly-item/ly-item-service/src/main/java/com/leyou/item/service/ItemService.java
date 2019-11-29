package com.leyou.item.service;
import	java.util.Random;

import com.leyou.item.pojo.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    public Item saveItem(Item item){
        int i = new Random().nextInt(100);
        item.setId(i);
        return item;
    }
}
